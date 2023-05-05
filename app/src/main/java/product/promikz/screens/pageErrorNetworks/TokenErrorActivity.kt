package product.promikz.screens.pageErrorNetworks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import product.promikz.R
import product.promikz.screens.refresh.RefreshActivity
import product.promikz.databinding.ActivityTokenErrorBinding

class TokenErrorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTokenErrorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTokenErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRefresh.setOnClickListener {
            val intent = Intent(this, RefreshActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
            this.finish()
        }
    }
}