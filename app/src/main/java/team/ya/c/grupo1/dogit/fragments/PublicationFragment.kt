package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.databinding.FragmentHomeBinding
import team.ya.c.grupo1.dogit.databinding.FragmentPublicationBinding
import team.ya.c.grupo1.dogit.entities.DogEntity
import java.util.UUID

class PublicationFragment : Fragment() {

    private var _binding: FragmentPublicationBinding? = null
    private val binding get() = _binding!!
    private lateinit var view : View
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicationBinding.inflate(inflater, container, false)
        view = binding.root
        startSpinners()

        return view
    }

    override fun onStart() {
        super.onStart()

        binding.btnPublicationDogSave.setOnClickListener {
            savePublication()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun makeAdapterSpinner(list :  MutableList<String>, hint : String): ArrayAdapter<String> {
        list.add(0, hint)

        val adapter = object : ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_activated_1, list){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }

        return adapter
    }

    //TODO: agregar razas y subrazas al spinner correspondiente
    private fun startSpinners(){
        val provincesList = resources.getStringArray(R.array.provinces_array).toMutableList()
        val provincesHint = resources.getString(R.string.publicationSelectLocation)
        binding.spinnerPublicationDogUbication.adapter = makeAdapterSpinner(provincesList, provincesHint)

        val genderList = resources.getStringArray(R.array.gender_array).toMutableList()
        val genderHint = resources.getString(R.string.publicationSelectGender)
        binding.spinnerPublicationDogGender.adapter = makeAdapterSpinner(genderList, genderHint)
    }

    private fun savePublication(){
        val publication = createPublication() ?: return
        savePublicationToDatabase(publication)
    }

    //TODO: poner nombre de usuario logueado en adopterName
    //TODO: agregar raza y sub raza
    //TODO: subir múltiples imágenes
    //TODO: validar campos
    private fun createPublication() : DogEntity?{
        val adopterName = "Martin"
        val name = binding.editTxtPublicationDogName.text.toString()
        val location = binding.spinnerPublicationDogUbication.selectedItem.toString()
        val age = binding.editTxtPublicationDogAge.text.toString().toIntOrNull() ?: 0
        val weight = binding.editTxtPublicationDogWeight.text.toString().toDoubleOrNull() ?: 0.0
        val gender = binding.spinnerPublicationDogGender.selectedItem.toString()
        val images = mutableListOf<String>()
        images.add(binding.editTxtPublicationDogPicture.text.toString())
        val race = "Golden"
        val subrace = "Canchero"
        val description = binding.editTxtPublicationDogDescription.text.toString()
        val id = UUID.randomUUID().toString()

        val dog = DogEntity(name, race, subrace, age, gender, description, weight, location, images, adopterName, id)

        return if (validateFields(dog)) dog else null
    }

    //TODO: validar todos los campos de URL de imágenes
    private fun validateFields(publication: DogEntity) : Boolean{
        val fieldsToCheck = listOf(
            Pair(publication.name, resources.getString(R.string.publicationErrorName)),
            Pair(publication.location, resources.getString(R.string.publicationErrorLocation)),
            Pair(publication.age, resources.getString(R.string.publicationErrorAge)),
            Pair(publication.weight, resources.getString(R.string.publicationErrorWeight)),
            Pair(publication.gender, resources.getString(R.string.publicationErrorGender)),
            Pair(publication.images[0], resources.getString(R.string.publicationErrorImages)),
            Pair(publication.race, resources.getString(R.string.publicationErrorRace)),
            Pair(publication.subrace, resources.getString(R.string.publicationErrorSubrace)),
            Pair(publication.description, resources.getString(R.string.publicationErrorDescription))
        )

        for ((property, errorMessage) in fieldsToCheck){
            if (property.toString().isNullOrEmpty() || property.toString().isBlank()){
                Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show()
                return false
            }
        }

        if (publication.location == resources.getString(R.string.publicationSelectLocation)){
            Snackbar.make(view, resources.getString(R.string.publicationErrorLocation), Snackbar.LENGTH_LONG).show()
            return false
        }

        if (publication.gender == resources.getString(R.string.publicationSelectGender)){
            Snackbar.make(view, resources.getString(R.string.publicationErrorGender), Snackbar.LENGTH_LONG).show()
            return false
        }

        if (publication.age == 0){
            Snackbar.make(view, resources.getString(R.string.publicationErrorAge), Snackbar.LENGTH_LONG).show()
            return false
        }

        if (publication.weight == 0.0){
            Snackbar.make(view, resources.getString(R.string.publicationErrorWeight), Snackbar.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun savePublicationToDatabase(publication: DogEntity){
        val documentReference = db.collection("dogs").document(publication.id)

        documentReference.set(publication)
            .addOnSuccessListener {
                safeAccessBinding {
                    Toast.makeText(view.context, resources.getString(R.string.publicationSuccesfulMessage), Toast.LENGTH_LONG).show()
                    view.findNavController().popBackStack()
                }
            }
            .addOnFailureListener {
                safeAccessBinding {
                    Snackbar.make(view, resources.getString(R.string.publicationErrorMessage), Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun safeAccessBinding(action: () -> Unit) {
        if (_binding != null && context != null) {
            action()
        }
    }

}