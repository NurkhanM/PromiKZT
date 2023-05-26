package product.promikz.screens.pageErrorNetworks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import product.promikz.databinding.ErrorLinkServerBinding

class ErrorLinkServerActivity : AppCompatActivity() {

    lateinit var binding: ErrorLinkServerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ErrorLinkServerBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }

}