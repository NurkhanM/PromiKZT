package product.promikz.screens.pageErrorNetworks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import product.promikz.databinding.ActivityServerErrorBinding
import kotlin.system.exitProcess

class ServerErrorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityServerErrorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seExit.setOnClickListener {
            exitProcess(0)
        }
    }
}