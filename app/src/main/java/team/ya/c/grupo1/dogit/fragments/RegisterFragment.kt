package team.ya.c.grupo1.dogit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.databinding.FragmentRegisterBinding
import kotlin.properties.Delegates

class RegisterFragment : Fragment() {

    private lateinit var v : View

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var continueButton: Button

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        v = binding.root
        return v
    }

    override fun onStart() {
        super.onStart()

        continueButton = binding.btnRegisterContinue
        continueButton.setOnClickListener {
            register()
        }
    }

    private fun register() {
        etEmail = binding.editTxtRegisterEmail
        etPassword = binding.editTxtRegisterPassword

        email = etEmail.text.toString()
        password = etPassword.text.toString()

        if(!email.isEmpty() && !password.isEmpty() && isEmailValid(email)){
            val action = RegisterFragmentDirections.actionRegisterFragmentToRegisterDataFragment(email, password)
            findNavController().navigate(action)
        } else {
            Toast.makeText(activity, resources.getString(R.string.registerWrongFormat), Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}