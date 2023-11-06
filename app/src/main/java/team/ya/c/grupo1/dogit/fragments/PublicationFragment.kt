package team.ya.c.grupo1.dogit.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.apiInterface.ApiService
import team.ya.c.grupo1.dogit.databinding.FragmentPublicationBinding
import team.ya.c.grupo1.dogit.entities.DogEntity
import java.util.Date
import java.util.UUID

class PublicationFragment : Fragment() {

    private var _binding: FragmentPublicationBinding? = null
    private val binding get() = _binding!!
    private lateinit var view : View
    private val db = FirebaseFirestore.getInstance()
    private var breedMap = mutableMapOf<String, List<String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublicationBinding.inflate(inflater, container, false)
        view = binding.root

        return view
    }

    override fun onStart() {
        super.onStart()
        setupButtons()
        startBreedMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupButtons(){
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

    private fun startBreedMap(){
        binding.scrollViewPublication.visibility = View.GONE
        binding.progressBarPublication.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java).getDogsAllBreed("api/breeds/list/all")
            val dogs = call.body()
            breedMap = dogs?.dogBreeds?.toMutableMap()?: mutableMapOf()

            requireActivity().runOnUiThread{
                startSpinners()
                binding.scrollViewPublication.visibility = View.VISIBLE
                binding.progressBarPublication.visibility = View.GONE
            }
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

    @SuppressLint("DiscouragedApi")
    private fun addImgAddPhoto(index: Int) {
        val imgId = resources.getIdentifier("imgPublicationDogAddPhoto$index", "id", requireActivity().packageName)
        val imgToRemove = view.findViewById<ImageView>(imgId)
        imgToRemove.visibility = View.VISIBLE
    }

    @SuppressLint("DiscouragedApi")
    private fun removeEditTextPhoto(index: Int) {
        val linearLayoutId = resources.getIdentifier("linearLayoutPublicationDogPhoto$index", "id", requireActivity().packageName)
        val linearLayoutToAdd = view.findViewById<LinearLayout>(linearLayoutId)
        linearLayoutToAdd.visibility = View.GONE
    }

    private fun startSpinners(){
        val provincesList = resources.getStringArray(R.array.provincesArray).toMutableList()
        val provincesHint = resources.getString(R.string.publicationSelectLocation)
        provincesList.add(0, provincesHint)
        binding.spinnerPublicationDogUbication.adapter = makeAdapterSpinner(provincesList)

        val genderList = resources.getStringArray(R.array.genderArray).toMutableList()
        val genderHint = resources.getString(R.string.publicationSelectGender)
        genderList.add(0, genderHint)
        binding.spinnerPublicationDogGender.adapter = makeAdapterSpinner(genderList)

        val breedList = breedMap.keys.toMutableList()
        val breedHint = resources.getString(R.string.publicationSelectBreed)
        breedList.add(0, breedHint)
        binding.spinnerPublicationDogBreed.adapter = makeAdapterSpinner(breedList)

        binding.spinnerPublicationDogBreed.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                val breedSelected = binding.spinnerPublicationDogBreed.selectedItem.toString()
                val subBreedList = breedMap[breedSelected]?.toMutableList() ?: mutableListOf()
                val subBreedHint = resources.getString(R.string.publicationSelectSubBreed)
                subBreedList.add(0, subBreedHint)
                binding.spinnerPublicationDogSubBreed.adapter = makeAdapterSpinner(subBreedList)

                binding.spinnerPublicationDogSubBreed.isEnabled = subBreedList.size > 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun makeAdapterSpinner(list : MutableList<String>): ArrayAdapter<String> {
        val adapter = object : ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_activated_1, list){

            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)

                if (position == 0) {
                    (view as TextView).setTextColor(resources.getColor(R.color.grey, null))
                    view.setBackgroundColor(0)
                }
                return view
            }
        }

        return adapter
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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
        val breed = binding.spinnerPublicationDogBreed.selectedItem.toString()
        var subBreed = binding.spinnerPublicationDogSubBreed.selectedItem.toString()
        val description = binding.editTxtPublicationDogDescription.text.toString()
        val id = UUID.randomUUID().toString()
        val adopterEmail = ""
        val ownerEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val publicationDate = Date()

        if (subBreed == resources.getString(R.string.publicationSelectSubBreed)){
            subBreed = ""
        }

        val dog = DogEntity(name, breed, subBreed, age, gender, description, weight, location, images,
            adopterName, id, mutableListOf<String>(), adopterEmail, ownerEmail, publicationDate)

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

        if (publication.breed == resources.getString(R.string.publicationSelectBreed)){
            Toast.makeText(view.context, resources.getString(R.string.publicationErrorBreed), Toast.LENGTH_LONG).show()
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
                    emptyFields()
                }
            }
            .addOnFailureListener {
                safeAccessBinding {
                    Snackbar.make(view, resources.getString(R.string.publicationErrorMessage), Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun emptyFields() {
        binding.editTxtPublicationDogName.text = Editable.Factory.getInstance().newEditable("")
        binding.spinnerPublicationDogUbication.setSelection(0)
        binding.editTxtPublicationDogAge.text = Editable.Factory.getInstance().newEditable("")
        binding.editTxtPublicationDogWeight.text = Editable.Factory.getInstance().newEditable("")
        binding.spinnerPublicationDogGender.setSelection(0)
        binding.spinnerPublicationDogBreed.setSelection(0)
        binding.spinnerPublicationDogSubBreed.setSelection(0)
        binding.editTxtPublicationDogDescription.text = Editable.Factory.getInstance().newEditable("")
        binding.editTxtPublicationDogPhoto1.text = Editable.Factory.getInstance().newEditable("")
        binding.editTxtPublicationDogPhoto2.text = Editable.Factory.getInstance().newEditable("")
        binding.editTxtPublicationDogPhoto3.text = Editable.Factory.getInstance().newEditable("")
        binding.editTxtPublicationDogPhoto4.text = Editable.Factory.getInstance().newEditable("")
        binding.editTxtPublicationDogPhoto5.text = Editable.Factory.getInstance().newEditable("")

        removeEditTextPhoto(2)
        removeEditTextPhoto(3)
        removeEditTextPhoto(4)
        removeEditTextPhoto(5)
        removeImgAddPhoto(2)
        removeImgAddPhoto(3)
        removeImgAddPhoto(4)
        addImgAddPhoto(1)
    }

    private fun safeAccessBinding(action: () -> Unit) {
        if (_binding != null && context != null) {
            action()
        }
    }

}