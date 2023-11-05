package team.ya.c.grupo1.dogit.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.activities.MainActivity
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
    ): View {
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

        binding.imgPublicationDogAddPhoto1.setOnClickListener {
            addEditTextPhoto(2)
            removeImgAddPhoto(1)
        }

        binding.imgPublicationDogAddPhoto2.setOnClickListener {
            addEditTextPhoto(3)
            removeImgAddPhoto(2)
        }

        binding.imgPublicationDogAddPhoto3.setOnClickListener {
            addEditTextPhoto(4)
            removeImgAddPhoto(3)
        }

        binding.imgPublicationDogAddPhoto4.setOnClickListener {
            addEditTextPhoto(5)
            removeImgAddPhoto(4)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun removeImgAddPhoto(index: Int) {
        val imgId = resources.getIdentifier("imgPublicationDogAddPhoto$index", "id", requireActivity().packageName)
        val imgToRemove = view.findViewById<ImageView>(imgId)
        imgToRemove.visibility = View.GONE
    }

    @SuppressLint("DiscouragedApi")
    private fun addEditTextPhoto(index: Int) {
        val linearLayoutId = resources.getIdentifier("linearLayoutPublicationDogPhoto$index", "id", requireActivity().packageName)
        val linearLayoutToAdd = view.findViewById<LinearLayout>(linearLayoutId)
        linearLayoutToAdd.visibility = View.VISIBLE
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

    //TODO: agregar razas y subrazas traidos desde retrofit
    private fun startSpinners(){
        val provincesList = resources.getStringArray(R.array.provincesArray).toMutableList()
        val provincesHint = resources.getString(R.string.publicationSelectLocation)
        binding.spinnerPublicationDogUbication.adapter = makeAdapterSpinner(provincesList, provincesHint)

        val genderList = resources.getStringArray(R.array.genderArray).toMutableList()
        val genderHint = resources.getString(R.string.publicationSelectGender)
        binding.spinnerPublicationDogGender.adapter = makeAdapterSpinner(genderList, genderHint)

        val raceList = resources.getStringArray(R.array.genderArray).toMutableList()
        val raceHint = resources.getString(R.string.publicationSelectRace)
        binding.spinnerPublicationDogRace.adapter = makeAdapterSpinner(raceList, raceHint)

        val subraceList = resources.getStringArray(R.array.genderArray).toMutableList()
        val subraceHint = resources.getString(R.string.publicationSelectSubrace)
        binding.spinnerPublicationDogSubrace.adapter = makeAdapterSpinner(subraceList, subraceHint)
    }

    private fun savePublication(){
        val publication = createPublication() ?: return
        savePublicationToDatabase(publication)
    }

    private fun createPublication() : DogEntity?{
        val adopterName = ""
        val name = binding.editTxtPublicationDogName.text.toString()
        val location = binding.spinnerPublicationDogUbication.selectedItem.toString()
        val age = binding.editTxtPublicationDogAge.text.toString().toIntOrNull() ?: 0
        val weight = binding.editTxtPublicationDogWeight.text.toString().toDoubleOrNull() ?: 0.0
        val gender = binding.spinnerPublicationDogGender.selectedItem.toString()
        val images = getPhotosFromEditText()
        val race = binding.spinnerPublicationDogRace.selectedItem.toString()
        val subrace = binding.spinnerPublicationDogSubrace.selectedItem.toString()
        val description = binding.editTxtPublicationDogDescription.text.toString()
        val id = UUID.randomUUID().toString()
        val adopterEmail = ""
        val ownerEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        val dog = DogEntity(name, race, subrace, age, gender, description, weight, location, images, adopterName, id, mutableListOf<String>(), adopterEmail, ownerEmail)

        return if (validateFields(dog)) dog else null
    }

    private fun getPhotosFromEditText(): MutableList<String> {
        val photos = mutableListOf<String>()

        for (i in 1..5) {
            val editTextId = resources.getIdentifier("editTxtPublicationDogPhoto$i", "id", requireActivity().packageName)
            val editTextComponent = view.findViewById<EditText>(editTextId)
            val linearLayoutId = resources.getIdentifier("linearLayoutPublicationDogPhoto$i", "id", requireActivity().packageName)
            val linearLayoutComponent = view.findViewById<LinearLayout>(linearLayoutId)

            if(linearLayoutComponent.visibility == View.VISIBLE) {
                photos.add(editTextComponent.text.toString())
            }
        }

        return photos
    }

    private fun validateFields(publication: DogEntity) : Boolean{
        val fieldsToCheck = listOf(
            Pair(publication.name, resources.getString(R.string.publicationErrorName)),
            Pair(publication.age, resources.getString(R.string.publicationErrorAge)),
            Pair(publication.weight, resources.getString(R.string.publicationErrorWeight)),
            Pair(publication.description, resources.getString(R.string.publicationErrorDescription))
        )

        for ((property, errorMessage) in fieldsToCheck){
            if (property.toString().isEmpty() || property.toString().isBlank()){
                Toast.makeText(view.context, errorMessage, Toast.LENGTH_LONG).show()
                return false
            }
        }

        for(image in publication.images) {
            if (image.isEmpty() || image.isBlank()){
                Toast.makeText(view.context, resources.getString(R.string.publicationErrorImages), Toast.LENGTH_LONG).show()
                return false
            }
        }

        if (publication.location == resources.getString(R.string.publicationSelectLocation)){
            Toast.makeText(view.context, resources.getString(R.string.publicationErrorLocation), Toast.LENGTH_LONG).show()
            return false
        }

        if (publication.gender == resources.getString(R.string.publicationSelectGender)){
            Toast.makeText(view.context, resources.getString(R.string.publicationErrorGender), Toast.LENGTH_LONG).show()
            return false
        }

        if (publication.race == resources.getString(R.string.publicationSelectRace)){
            Toast.makeText(view.context, resources.getString(R.string.publicationErrorRace), Toast.LENGTH_LONG).show()
            return false
        }

        if (publication.subrace == resources.getString(R.string.publicationSelectSubrace)){
            Toast.makeText(view.context, resources.getString(R.string.publicationErrorSubrace), Toast.LENGTH_LONG).show()
            return false
        }

        if (publication.age == 0 || publication.age > 30){
            Toast.makeText(view.context, resources.getString(R.string.publicationErrorAge), Toast.LENGTH_LONG).show()
            return false
        }

        if (publication.weight == 0.0 || publication.weight > 200){
            Toast.makeText(view.context, resources.getString(R.string.publicationErrorWeight), Toast.LENGTH_LONG).show()
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