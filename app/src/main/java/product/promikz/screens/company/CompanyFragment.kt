package product.promikz.screens.company

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import product.promikz.AppConstants.ID_CHAD_BOT
import product.promikz.AppConstants.SUPPORT_EMAIL
import product.promikz.AppConstants.SUPPORT_PHONE
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentCompanyBinding
import product.promikz.viewModels.HomeViewModel

class CompanyFragment : Fragment() {

    private var _binding: FragmentCompanyBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCompanyBinding.inflate(inflater, container, false)

        binding.nBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.phoneSupport.setOnClickListener {
            sendPhone()
        }

        binding.emailSupport.setOnClickListener {
            sendEmail()
        }

        viewModel.getUser("Bearer $TOKEN_USER")

        binding.btnSend.setOnClickListener {

            binding.editSupport.visibility = View.GONE
            binding.btnSend.visibility = View.GONE
            binding.textDescription.text =
                "Мы примем ваши пожелания во внимание и свяжемся с вами, если у нас возникнут дополнительные вопросы."
            uToast(requireContext(), "Отправлено")


            viewModel.myUser.observe(viewLifecycleOwner) { info ->
                if (info.isSuccessful) {

                    val telegramMessage =
                            "${info.body()?.data?.name}\n" +
                            "${info.body()?.data?.phone}\n" +
                            "${info.body()?.data?.email}\n" +
                            "Тип акк ${info.body()?.data?.status}\n\n"

                    viewModel.sendRequestMessageBot(
                        ID_CHAD_BOT,
                        requestTelegramMessage(binding.editSupport.text.toString(), telegramMessage)
                    )

                }
            }

            binding.editSupport.setText("")
        }


        binding.textSup.text = SUPPORT_PHONE
        binding.textEmail.text = SUPPORT_EMAIL

        return binding.root
    }


    private fun sendPhone() {
        val url = "https://api.whatsapp.com/send?phone=$SUPPORT_PHONE"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun sendEmail() {
        val subject = "Тема письма"
        val body = "Текст письма"

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$SUPPORT_EMAIL")
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(intent)
    }

    private fun requestTelegramMessage(description: String, telegramMessage: String): String {


        return "Promi: ->\n" +
                telegramMessage +
                "Комментарии : \n" +
                description


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}