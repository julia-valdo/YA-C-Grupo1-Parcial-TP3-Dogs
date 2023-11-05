package team.ya.c.grupo1.dogit.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.activities.MainActivity
import team.ya.c.grupo1.dogit.databinding.FragmentLoginBinding
import kotlin.properties.Delegates

class LoginFragment : Fragment() {
    companion object {
        lateinit var userEmail: String
        lateinit var providerSession: String
    }

    private lateinit var v : View

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginButton: Button

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        v = binding.root

        return v
    }

    override fun onStart() {
        super.onStart()

        mAuth = FirebaseAuth.getInstance()

        loginButton = binding.btnLoginLogin
        loginButton.setOnClickListener {
            login()
        }

        val btnTextRegister = binding.btnTxtLoginRegister
        btnTextRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        val btnForgotPassword = binding.txtLoginForgotPassword
        btnForgotPassword.setOnClickListener {
            forgotPassword()
        }
    }

    private fun login() {
        email = binding.editTxtLoginEmail.text.toString()
        password = binding.editTxtLoginPassword.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, resources.getString(R.string.loginEmptyFields), Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    goHome(email, "email")
                    requireActivity().finish()
                }
                else Toast.makeText(activity, resources.getString(R.string.loginInvalidCredentials), Toast.LENGTH_SHORT).show()
        }
    }

    private fun forgotPassword() {
        resetPassword()
    }

    private fun resetPassword() {
        val email = binding.editTxtLoginEmail.text.toString()
        if(!TextUtils.isEmpty(email)) {
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        Toast.makeText(activity, resources.getString(R.string.loginResetPassword), Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(activity, resources.getString(R.string.loginResetPasswordError), Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else {
            Toast.makeText(activity, resources.getString(R.string.loginEmptyEmail), Toast.LENGTH_SHORT).show()
        }
    }

    private fun goHome(email: String, provider: String) {
        userEmail = email
        providerSession = provider

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)

        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}