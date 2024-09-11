package com.hfad.translatorapplication

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.util.Locale

class MainActivity : AppCompatActivity() {
    // UI-komponenter
    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var sourceEdt: EditText
    private lateinit var micIV: ImageView
    private lateinit var translateBtn: MaterialButton
    private lateinit var translatedTV: TextView


    // Arrayer asv tillgängliga språk
    private val fromLanguages = arrayOf(
        "From", "English", "Arabic", "Swedish", "Norwegian", "Danish", "Czech",
        "Welsh", "Hindi", "Urdu"
    )

    private val toLanguages = arrayOf(
        "To", "English", "Arabic", "Swedish", "Norwegian", "Danish", "Czech",
        "Welsh", "Hindi", "Urdu"
    )

    companion object {
        const val REQUEST_PERMISSION_CODE = 1
    }


    //  Valda språk som är används för att spara postion i arryn
    private var fromLanguageCode: Int = 0
    private var toLanguageCode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisera UI-komponenter
        initializeViews()
        // Ställ in språkspinnerar
        setupSpinners()
        // Ställ in klicklyssnare för översättningsknappen
        setupTranslateButton()
        // Ställ in klicklyssnare för mikrofonknappen
        setupMicButton()
    }
    // Initialisera UI-komponenter
    private fun initializeViews() {
        fromSpinner = findViewById(R.id.spinnerID)
        toSpinner = findViewById(R.id.idToSpinner)
        sourceEdt = findViewById(R.id.idEdtSource)
        micIV = findViewById(R.id.micImage)
        translateBtn = findViewById(R.id.idBtnTranslate)
        translatedTV = findViewById(R.id.idTVTranslatedTV)
    }

    private fun setupSpinners() {
        // Skapa en adapter för språklistan och anslut den till Spinner
        val fromAdapter = ArrayAdapter(this, R.layout.spinner_item, fromLanguages)
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner.adapter = fromAdapter

        val toAdapter = ArrayAdapter(this, R.layout.spinner_item, toLanguages)
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toSpinner.adapter = toAdapter

        // Skapa lyssnare för när användaren väljer ett språk från Spinner och hämta dess språk
        setupSpinnerListener(fromSpinner) { fromLanguageCode = getLanguageCode(fromLanguages[it]) }
        setupSpinnerListener(toSpinner) { toLanguageCode = getLanguageCode(toLanguages[it]) }
    }

    private fun setupSpinnerListener(spinner: Spinner, onItemSelected: (Int) -> Unit) {
        // Ställ in en lyssnare för när användaren väljer ett objekt från Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Hantera händelsen när ett objekt väljs
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // Anropa den förutbestämda funktionen när ett objekt väljs och skicka med platsen av det valda objektet
                onItemSelected(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing here if needed
            }

        }
    }
    // Funktion för att ställa in översättningsknappen
    private fun setupTranslateButton() {
        // Lägg till en lyssnare för när användaren klickar på översättningsknappen
        translateBtn.setOnClickListener {
            // Rensa översättningen från tidigare
            translatedTV.text = ""
            // Kolla om användaren har angett text att översätta
            when {
                // Visa meddelande om text saknas
                sourceEdt.text.toString().isEmpty() -> showToast("Please enter text to translate")
                // Visa meddelande om källspråk inte är valt
                fromLanguageCode == 0 -> showToast("Please select source language")
                // Visa meddelande om översättningsspråk inte är valt
                toLanguageCode == 0 -> showToast("Please select language to translate")
                // Om allt är i ordning, översätt texten

                // Anropa translateText-funktionen för att påbörja översättningsprocessen.
                // Vi skickar med tre viktiga parametrar:
                //   1. fromLanguageCode: Koden för det valda källspråket.
                //   2. toLanguageCode: Koden för det valda översättningsspråket.
                //   3. sourceEdt.text.toString(): Den text som användaren har skrivit in och vill översätta.
                // Detta initierar översättningen från källspråket till översättningsspråket.
                else -> translateText(fromLanguageCode, toLanguageCode, sourceEdt.text.toString())
            }
        }
    }
    // Funktion för att ställa in mikrofonknappen
    private fun setupMicButton() {
        // Lägg till en lyssnare för när användaren klickar på mikrofonknappen
        micIV.setOnClickListener {
            // Starta rösttolkning när mikrofonknappen klickas
            startSpeechRecognition()
        }
    }
    // Funktion för att starta rösttolkning
    private fun startSpeechRecognition() {
        // Skapa en Intent för att initiera taligenkänning
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Ange rösttolkning:
        // - EXTRA_PROMPT: Ett meddelande som visas under taligenkänningen för att instruera användaren
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        // - Locale.getDefault(): Använder enhetens aktuella språk för taligenkänning
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        // - LANGUAGE_MODEL_FREE_FORM: Fritt taligenkänningsläge utan förutbestämd grammatik
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text")
        // Försök starta rösttolkning genom att starta en aktivitet med Intent
        try {
            startActivityForResult(intent, REQUEST_PERMISSION_CODE)
        } catch (e: Exception) {
            // Vid eventuella fel, skriv ut felmeddelandet och visa det som ett kort meddelande
            e.printStackTrace()
            showToast("" + e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PERMISSION_CODE && resultCode == RESULT_OK) {
            // Get the results from the speech recognition
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

            if (results != null && results.isNotEmpty()) {
                // Assuming you want to take the first result as the user's speech
                val spokenText = results[0]

                // Update the sourceEdt with the spoken text
                sourceEdt.setText(spokenText)
            }
        }
    }

    // Funktion för att översätta text från ett språk till ett annat
    private fun translateText(fromLanguageCode: Int, toLanguageCode: Int, source: String) {
        // Uppdatera användargränssnittet för att visa att modellen laddas ner
        translatedTV.text = "Downloading model"
        // Skapa inställningar för översättaren med källspråk och översättningsspråk
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(fromLanguageCode)
            .setTargetLanguage(toLanguageCode)
            .build()

        // Skapa en översättare med hjälp av inställningarna
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        // Skapa villkor för modellnedladdning
        val conditions = FirebaseModelDownloadConditions.Builder().build()


        // Försök ladda ner modellen vid behov och initiera översättningen
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            // Uppdatera användargränssnittet för att visa att översättningen pågår
            translatedTV.text = "Translating..."
            // Översätt källtexten
            translator.translate(source).addOnSuccessListener { result ->
                // Uppdatera användargränssnittet med den översatta texten
                translatedTV.text = result
            }.addOnFailureListener { e ->
                // Vid fel, visa felmeddelande
                showToast("Failed to translate: ${e.message}")
            }
        }.addOnFailureListener { e ->
            // när modellnedladdning inte funkar, visa felmeddelande
            showToast("Failed to download language: ${e.message}")
        }
    }
// Funktion för att visa korta meddelanden (Toast) på användargränssnittet
    private fun showToast(message: String) {
    // Skapa och visa en kort Toast meddelande
    // - this@MainActivity: Aktuell aktivitetsreferens där Toast-meddelandet ska visas
    // - message: Det meddelande som ska visas för användaren
    // - Toast.LENGTH_SHORT: Varaktighet för Toast-meddelandet (kortvarig)
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    // Funktion för att hämta språkkod baserat på användarens valda språk och tar in der som Parametrar
    // language: Användarens valda språk som en sträng
    // Senare Returnerar Språkkoden för det valda språket som en heltalsvärde
    private fun getLanguageCode(language: String): Int {
        // Använd 'when' uttryck för att matcha användarens valda språk och returnera motsvarande språkkod
        return when (language) {
            "English" -> FirebaseTranslateLanguage.EN
            "Arabic" -> FirebaseTranslateLanguage.AR
            "Swedish" -> FirebaseTranslateLanguage.SV
            "Norwegian" -> FirebaseTranslateLanguage.NO
            "Danish" -> FirebaseTranslateLanguage.DA
            "Czech" -> FirebaseTranslateLanguage.CY
            "Welsh" -> FirebaseTranslateLanguage.SW
            "Hindi" -> FirebaseTranslateLanguage.HI
            "Urdu" -> FirebaseTranslateLanguage.UR
            else -> 0
            // Returnera 0 om språket inte matchar något av de fördefinierade språken
        }
    }
}
