package com.example.basketballcounter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.io.File
import java.util.*


private const val TAG = "BasketballCounter"
private const val G_IDX = "GAME_IDX"
private const val REQUEST_PHOTO_A = 0
private const val REQUEST_PHOTO_B = 1

class BasketballCounterFragment() : Fragment() {
    private lateinit var pointLabelA: TextView
    private lateinit var pointLabelB: TextView
    private lateinit var teamLabelA: TextView
    private lateinit var teamLabelB: TextView
    private lateinit var dateLabel: TextView
    private lateinit var teamACamera: ImageButton
    private lateinit var teamBCamera: ImageButton
    private lateinit var teamALogo: ImageView
    private lateinit var teamBLogo: ImageView
    private lateinit var photoFileA: File
    private lateinit var photoFileB: File
    private lateinit var photoURIA: Uri
    private lateinit var photoURIB: Uri
    private lateinit var soundManager: SoundManager
    private lateinit var mediaPlayer: MediaPlayer
    private var thisGame: Game = Game()
    private val gameDetailViewModel: GameDetailViewModel by lazy {
        ViewModelProviders.of(this).get(GameDetailViewModel::class.java)
    }
    private var dynamicLoad: Boolean = false

    interface Callbacks {
        fun onDisplayClick(winnerA: Boolean)
    }

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
        Log.d(TAG, "BC Fragment set callbacks in onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
        requireActivity().revokeUriPermission(photoURIA,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        requireActivity().revokeUriPermission(photoURIB,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        mediaPlayer.release()
        Log.d(TAG, "BC Fragment removed callbacks and revoked URI permissions in onDetach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "BC fragment created")

        soundManager = context?.assets?.let { SoundManager(it) }!!
        mediaPlayer = MediaPlayer.create(context, R.raw.cheer1)

        arguments?.getString(G_IDX)?.let {
            gameDetailViewModel.loadGame(UUID.fromString(it))
            dynamicLoad = true
            Log.d(TAG, "BC fragment received UUID: $it")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "BC fragment view inflated")
        val view = inflater.inflate(R.layout.fragment_basketballteam, container, false)
        teamLabelA = view.findViewById(R.id.team_label_a)
        teamLabelB = view.findViewById(R.id.team_label_b)
        pointLabelA = view.findViewById(R.id.point_label_a)
        pointLabelB = view.findViewById(R.id.point_label_b)
        dateLabel = view.findViewById(R.id.game_label)
        teamACamera = view.findViewById(R.id.team_a_camera)
        teamBCamera = view.findViewById(R.id.team_b_camera)
        teamALogo = view.findViewById(R.id.team_a_logo)
        teamBLogo = view.findViewById(R.id.team_b_logo)
        updateUI()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoFileA = gameDetailViewModel.getPhotoFile(thisGame, true)
        photoURIA = FileProvider.getUriForFile(requireActivity(), "com.example.basketballcounter.fileprovider", photoFileA)
        photoFileB = gameDetailViewModel.getPhotoFile(thisGame, false)
        photoURIB = FileProvider.getUriForFile(requireActivity(), "com.example.basketballcounter.fileprovider", photoFileB)
        gameDetailViewModel.gameLiveData.observe(
            viewLifecycleOwner,
            Observer { game ->
                game?.let {
                    this.thisGame = game
                    photoFileA = gameDetailViewModel.getPhotoFile(game, true)
                    photoFileB = gameDetailViewModel.getPhotoFile(game, false)
                    photoURIA = FileProvider.getUriForFile(requireActivity(),
                        "com.example.basketballcounter.fileprovider",
                        photoFileA)
                    photoURIB = FileProvider.getUriForFile(requireActivity(),
                        "com.example.basketballcounter.fileprovider",
                        photoFileB)
                    updateUI()
                    updatePhotoView(true)
                    updatePhotoView(false)
                }
            })
        Log.d(TAG, "BC fragment liveData set")
    }

    private fun updateUI(){
        teamLabelA.text = thisGame.teamAName
        teamLabelB.text = thisGame.teamBName
        pointLabelA.text = thisGame.teamAScore.toString()
        pointLabelB.text = thisGame.teamBScore.toString()
        dateLabel.text = "${Date(thisGame.date)}"
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "BC fragment started")

        view?.findViewById<Button>(R.id.three_point_a)?.setOnClickListener {
            updatePoints(  3, true)
        }

        view?.findViewById<Button>(R.id.three_point_b)?.setOnClickListener {
            updatePoints(  3, false)
        }

        view?.findViewById<Button>(R.id.two_point_a)?.setOnClickListener {
            updatePoints(  2, true)
        }

        view?.findViewById<Button>(R.id.two_point_b)?.setOnClickListener {
            updatePoints(  2, false)
        }

        view?.findViewById<Button>(R.id.free_throw_a)?.setOnClickListener {
            updatePoints(  1, true)
        }

        view?.findViewById<Button>(R.id.free_throw_b)?.setOnClickListener {
            updatePoints(  1, false)
        }

        view?.findViewById<Button>(R.id.reset)?.setOnClickListener {
            reset()
        }

        view?.findViewById<Button>(R.id.save_game)?.setOnClickListener {
            saveCurrentGame()
        }

        view?.findViewById<Button>(R.id.next_activity)?.setOnClickListener {
            showGameList()
        }

        view?.findViewById<ImageButton>(R.id.team_a_mic)?.setOnClickListener{
            mediaPlayer.start()
        }

        view?.findViewById<ImageButton>(R.id.team_b_mic)?.setOnClickListener{
            soundManager.play(soundManager.sounds[1])
        }

        teamACamera.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity.isEmpty()) {
                isEnabled = false
            }
            Log.d(TAG, "$resolvedActivity A is null?")
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoURIA)
                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(cameraActivity.activityInfo.packageName, photoURIA, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(captureImage, REQUEST_PHOTO_A)
            }
        }

        teamBCamera.apply{
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? = packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoURIB)
                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(cameraActivity.activityInfo.packageName, photoURIB, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(captureImage, REQUEST_PHOTO_B)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_PHOTO_A -> {
                requireActivity().revokeUriPermission(photoURIA,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView(true)
            }

            requestCode == REQUEST_PHOTO_B -> {
                requireActivity().revokeUriPermission(photoURIB,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView(false)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        saveCurrentGame()
    }

    private fun updatePhotoView(isA: Boolean){
        if(isA){
            if(photoFileA.exists()){
                val bitmap = getScaledBitmap(photoFileA.path, requireActivity())
                teamALogo.setImageBitmap(bitmap)
            } else {
                teamALogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_teama))
            }
        } else {
            if(photoFileB.exists()){
                val bitmap = getScaledBitmap(photoFileB.path, requireActivity())
                teamBLogo.setImageBitmap(bitmap)
            } else {
                teamBLogo.setImageDrawable(getResources().getDrawable(R.drawable.ic_teamb))
            }
        }
    }

    private fun reset(){
        thisGame.reset()
        updateTeamStanding(true)
        updateTeamStanding(false)
    }

    //increments the given team's points by the supplied amount, updates UI
    private fun updatePoints( points: Int, isA: Boolean){
        thisGame.increasePoints(points, isA)
        updateTeamStanding(isA)
    }

    //Changes one team's displayed points
    private fun updateTeamStanding(isA: Boolean){
        if(isA){
            pointLabelA.text = thisGame.teamAScore.toString()
        } else {
            pointLabelB.text = thisGame.teamBScore.toString()
        }
    }

    private fun showGameList(){
        Log.d(TAG, "BC Fragment display button clicked")
        callbacks?.onDisplayClick(thisGame.AisWinning())
    }

    private fun saveCurrentGame(){
        if(!dynamicLoad){
            gameDetailViewModel.addGame(thisGame)
            dynamicLoad = true
        } else {
            gameDetailViewModel.updateGame(thisGame)
        }
    }

    companion object {
        fun newInstance(id: UUID): BasketballCounterFragment {
            val args = Bundle().apply {
                putString(G_IDX, id.toString())
            }
            return BasketballCounterFragment().apply {
                arguments = args
            }
        }
    }
}
