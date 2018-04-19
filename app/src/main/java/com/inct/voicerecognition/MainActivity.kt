package com.inct.voicerecognition

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), RecognitionListener {

    companion object {
        const val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }

    private var speech: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    private val listResult: MutableList<MutableList<String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StartBtn.setOnClickListener {
            showStopBtn()
            startListeningSpeech()
            listResult.clear();
        }

        StopBtn.setOnClickListener {
            hideStopBtn()
            speech.stopListening()
        }

        permissionRequest()

        speech.setRecognitionListener(this)
    }

    fun showStopBtn() {
        StopBtn.visibility = View.VISIBLE
        StartBtn.visibility = View.GONE
    }
    fun hideStopBtn() {
        StartBtn.visibility = View.VISIBLE
        StopBtn.visibility = View.GONE
    }

    fun permissionRequest() {
        val permissionCheck = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_REQUEST_RECORD_AUDIO)
            return
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionRequest()
            } else {
                finish()
            }
        }
    }

    fun getTextFromMatches(): String {
        val stringBuilder = StringBuilder()
        listResult.forEach {
            stringBuilder.append(it[0])
        }
        return stringBuilder.toString()
    }

    fun startListeningSpeech() {
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)

        speech.startListening(recognizerIntent);
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.d("Recognation", "onReadyForSpeech")
    }

    override fun onRmsChanged(p0: Float) {
        Log.d("Recognation", "onRmsChanged")
    }

    override fun onBufferReceived(p0: ByteArray?) {
        Log.d("Recognation", "onBufferReceived")
    }

    override fun onPartialResults(p0: Bundle?) {
        Log.d("Recognation", "onPartialResults")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        Log.d("Recognation", "onEvent")
    }

    override fun onBeginningOfSpeech() {
        Log.d("Recognation", "onBeginningOfSpeech")
    }

    override fun onEndOfSpeech() {
        Log.d("Recognation", "onEndOfSpeech")
    }

    override fun onError(errorCode: Int) {
        val message: String;
        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO ->
                message = "ERROR_AUDIO"
            SpeechRecognizer.ERROR_CLIENT ->
                message = "ERROR_CLIENT"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                message = "ERROR_INSUFFICIENT_PERMISSIONS"
            SpeechRecognizer.ERROR_NETWORK ->
                message = "ERROR_NETWORK"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                message = "ERROR_NETWORK_TIMEOUT"
            SpeechRecognizer.ERROR_NO_MATCH ->
                message = "ERROR_NO_MATCH"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                message = "ERROR_RECOGNIZER_BUSY"
            SpeechRecognizer.ERROR_SERVER ->
                message = "ERROR_SERVER"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                message = "ERROR_SPEECH_TIMEOUT"
            else ->
                message = getString(R.string.error_understand)
        }
        Log.d("Recognation", "onError " + message)
    }

    override fun onResults(results: Bundle?) {
        Log.d("Recognation", "onResults")
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        listResult.add(matches)
        hideStopBtn()
        CaptionText.text = getTextFromMatches()
    }
}
