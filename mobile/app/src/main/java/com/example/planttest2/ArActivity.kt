package com.example.planttest2

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState

class ArActivity : AppCompatActivity() {
    lateinit var sceneView: ArSceneView
    private lateinit var modelNode: ArModelNode
    lateinit var placeButton: ExtendedFloatingActionButton

    // Ajouter ScaleGestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ar_layout)

        placeButton = findViewById(R.id.place)
        sceneView = findViewById(R.id.ar_scene)

        placeButton.setOnClickListener {
            placeModel()
        }

        modelNode = ArModelNode().apply {
            loadModelGlbAsync(
                glbFileLocation = "models/aloe_vera_plant.glb"
            ) {
                sceneView.planeRenderer.isVisible = true
            }
            onAnchorChanged = {
                placeButton.isGone = true
            }
        }

        sceneView.addChild(modelNode)

        // Initialiser ScaleGestureDetector
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Mise à jour du facteur de mise à l'échelle basé sur le geste de pincement
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(0.1f, 5.0f) // Limiter le zoom entre 0.1 et 5x
                modelNode.setScale(scaleFactor) // Appliquer l'échelle uniformément au modèle
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {}
        })

        // Détecter le touch et obtenir les coordonnées
        sceneView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event) // Passer l'événement au détecteur de mise à l'échelle
            if (event.action == MotionEvent.ACTION_UP) {
                placeModel(event)
            }
            true
        }
    }

    private fun placeModel(event: MotionEvent? = null) {
        // Assurez-vous que la session ARCore est valide
        val session = sceneView.arSession
        if (session != null) {
            val frame: Frame = session.update()

            // Vérifiez si le suivi est activé
            if (frame.camera.trackingState == TrackingState.TRACKING) {
                // Si l'utilisateur a touché l'écran, récupérez les coordonnées
                val screenX = event?.x?.toInt() ?: 0
                val screenY = event?.y?.toInt() ?: 0

                // Effectuer un hitTest avec les coordonnées obtenues
                val hitResult: HitResult? = getHitResult(frame, screenX, screenY)
                if (hitResult != null) {
                    // Créez l'ancre à partir du HitResult
                    val anchor = hitResult.createAnchor()
                    modelNode.anchor = anchor
                    sceneView.planeRenderer.isVisible = false // Masquer le rendu des plans
                } else {
                    showError("No valid hit detected. Try placing the model on a flat surface.")
                }
            } else {
                // Si le suivi ARCore n'est pas actif
                showError("AR tracking is not active.")
            }
        } else {
            showError("AR session is not initialized.")
        }
    }

    private fun getHitResult(frame: Frame, screenX: Int, screenY: Int): HitResult? {
        // Effectuer le hitTest et vérifier si le plan est détecté
        val hitResults = frame.hitTest(screenX.toFloat(), screenY.toFloat())
        if (hitResults.isNotEmpty()) {
            // Afficher des logs pour les résultats de hitTest
            for (hit in hitResults) {
                println("Hit detected: ${hit.trackable}")
            }
        } else {
            println("No hits detected at this position.")
        }

        return hitResults.firstOrNull {
            it.trackable is Plane && it.trackable.trackingState == TrackingState.TRACKING
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
