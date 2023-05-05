package product.promikz.screens.specialist.info

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.shop.SpecialistReviewBSH
import com.google.android.gms.common.api.ApiException
import product.promikz.AppConstants.SPECIALIST_ALL
import product.promikz.MyUtils
import product.promikz.MyUtils.uLogD
import product.promikz.databinding.FragmentInfoSpecialistBinding
import product.promikz.screens.complaint.specialist.ComplaintSpecialistActivity

class InfoSpecialistFragment : Fragment() {

    private var _binding: FragmentInfoSpecialistBinding? = null
    private val binding get() = _binding!!


    private lateinit var mImageArrayUpdate: HomeViewModel
    private var isLike: Boolean = false
    private var isMenu: Boolean = true

    private var nameProducts: String = ""
    private val url: String = "https://promi.kz/specialist/"
    private var urlProducts: String = ""

    private var idSpecialist: Int = -1
    private lateinit var vieww: View

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fab_rotate_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fab_rotate_close
        )
    }
    private val rotateFrom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fab_from_bottom
        )
    }
    private val rotateTo: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.fab_to_bottom
        )
    }
    private var clicked = false


    @Suppress("DEPRECATION")
    @SuppressLint("UseRequireInsteadOfGet", "CommitTransaction")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mImageArrayUpdate = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentInfoSpecialistBinding.inflate(inflater, container, false)
        val view = binding

        val arguments = (activity as AppCompatActivity).intent.extras
        idSpecialist = arguments!!["spec"] as Int
        SPECIALIST_ALL = arguments["spec"] as Int



        // Здесь проверка на Лайк
        view.updateImageFavorite.setOnClickListener {
                isLike = if (isLike) {
                    view.updateImageFavorite.setImageResource(R.drawable.ic_favorite)
                    false
                } else {
                    view.updateImageFavorite.setImageResource(R.drawable.ic_favorite2)
                    true
                }
            mImageArrayUpdate.postSpecialist("Bearer $TOKEN_USER", idSpecialist)
        }

        view.fabId.setOnClickListener {
            onAddButtonClicked()
        }

        view.btnSendRating.setOnClickListener {
            if (TOKEN_USER != "null") {
                val rating = view.isRatingUser.rating
                mImageArrayUpdate.postRating(
                    "Bearer $TOKEN_USER",
                    "specialist",
                    idSpecialist,
                    rating.toString())
                Toast.makeText(requireContext(), "$rating", Toast.LENGTH_SHORT).show()
                view.btnSendRating.visibility = View.GONE
                binding.textRating.text = "Вы поставили рейтинг к этому специалисту"
            } else {
                Toast.makeText(requireContext(), resources.getText(R.string.need_sign_up), Toast.LENGTH_SHORT)
                    .show()
            }

        }

        view.floatingActionButtonReview.setOnClickListener {
                (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .add(SpecialistReviewBSH(), "specialist")
                    .commit()

        }

        view.floatingActionButtonShare.setOnClickListener {
            showSharingDialogAsKotlinWithURL(nameProducts, urlProducts)
        }

        view.updateCallBtn.setOnClickListener {
            checkPermission()
        }

        view.clickUpdateBackCard.setOnClickListener {
            activity?.onBackPressed()
        }



        val resultCode1 = 103
        val resultCode2 = 104
        // We can do the assignment inside onAttach or onCreate
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // There are no request codes

                if (result.resultCode == resultCode1 && result.data != null) {
                    activity?.onBackPressed()
                } else if (result.resultCode == resultCode2) {
                    activity?.onBackPressed()
                }
            }



        view.itemMenu.setOnClickListener {
            if (isMenu) {
                view.showDopMenu.visibility = View.VISIBLE
                isMenu = false
            } else {
                view.showDopMenu.visibility = View.GONE
                isMenu = true
            }
        }


        view.nextComplaintProduct.setOnClickListener {
            val intent = Intent(requireActivity(), ComplaintSpecialistActivity::class.java)
            intent.putExtra("specID", idSpecialist)
            activityResultLauncher.launch(intent)

            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            view.showDopMenu.visibility = View.GONE
        }


        view.nextCopyLink.setOnClickListener {

            val clipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", urlProducts))

            MyUtils.uToast(requireContext(), "Copied")

            view.showDopMenu.visibility = View.GONE

        }


        return view.root
    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding?.updateAll?.visibility = View.GONE
        binding?.dopTextUpdate?.visibility = View.INVISIBLE
        binding?.UpdateWorkError?.visibility = View.VISIBLE


        try {
            mImageArrayUpdate.getSpecialistShow("Bearer $TOKEN_USER", idSpecialist)
            mImageArrayUpdate.mySpecialistShow.observe(viewLifecycleOwner) { list ->

                if (list.isSuccessful) {

                    MyUtils.uGlide(requireContext(), binding.imgAddFirst, list.body()?.data?.img)

                    binding.textNameUser.text = list.body()?.data?.user?.name
                    binding.textExperience.text = list.body()?.data?.experience.toString()
                    binding.textLocationInfo.text = list.body()?.data?.city?.name
                    binding.textNumberUser.text = list.body()?.data?.user?.phone
                    binding.textEmailUser.text = list.body()?.data?.user?.email

                    nameProducts = list.body()?.data?.user?.name.toString()
                    urlProducts = url + list.body()?.data?.id.toString()

                    if (list.body()?.data?.departure.toString() == "1"){
                        binding.textDeparture.text = resources.getText(R.string.ready_move)
                    }else {
                        binding.textDeparture.text = resources.getText(R.string.not)
                    }

                    if (list.body()?.data?.isRating == null) {
                        binding.isRatingUser.rating = 0.0f
                        binding.btnSendRating.visibility = View.VISIBLE
                    } else {
                        binding.isRatingUser.rating = list.body()?.data?.isRating!!
                        binding.textRating.text = "Вы поставили рейтинг к этому специалисту"
                        binding.btnSendRating.visibility = View.GONE
                    }


                    if (list.body()?.data?.skills?.isNotEmpty() == true){
                        val array = list.body()?.data?.skills!!
                        val arrayString = ArrayList<String>()
                        array.forEach{
                            arrayString.add(it.name)
                        }
                        binding.textSkills.text = arrayString.joinToString(prefix = "", postfix = "", separator = " / ") // <1•2•3•4•5•6>

                    }else{
                        binding.textSkills.text = resources.getText(R.string.not_skills)
                    }

                    if (list.body()?.data?.categories?.isNotEmpty() == true){
                        val array = list.body()?.data?.categories!!
                        val arrayString = ArrayList<String>()
                        array.forEach{
                            arrayString.add(it.name)
                        }
                        binding.textCategory.text = arrayString.joinToString(prefix = "", postfix = "", separator = " / ") // <1•2•3•4•5•6>

                    }else{
                        binding.textCategory.text = resources.getText(R.string.not_skills)
                    }

                    if (list.body()?.data?.specializations?.isNotEmpty() == true){
                        val array = list.body()?.data?.specializations!!
                        val arrayString = ArrayList<String>()
                        array.forEach{
                            arrayString.add(it.name)
                        }
                        binding.textSpecialization.text = arrayString.joinToString(prefix = "", postfix = "", separator = " / ") // <1•2•3•4•5•6>

                    }else{
                        binding.textSpecialization.text = resources.getText(R.string.not_skills)
                    }

                    if (list.body()?.data?.ratingsAvg == null) {
                        binding.svgRating.rating = 0.0f
                        binding.numberRatingAVG.text = resources.getText(R.string.not_rating)
                    } else {
                        binding.svgRating.rating =
                            list.body()?.data?.ratingsAvg!!
                        binding.numberRatingAVG.text = binding.svgRating.rating.toString()
                    }

                    if (list.body()?.data?.isRating == null) {
                        binding.isRatingUser.rating = 0.0f
                    } else {
                        binding.isRatingUser.rating =
                            list.body()?.data?.isRating!!
                    }

                    isLike = if (list.body()?.data?.isLike == false) {
                        binding.updateImageFavorite.setImageResource(R.drawable.ic_favorite)
                        false
                    } else {
                        binding.updateImageFavorite.setImageResource(R.drawable.ic_favorite2)
                        true
                    }

                    binding.idProduct?.text = " " + list.body()?.data?.id.toString()
                    binding.idCreate?.text = " " + list.body()?.data?.created_at.toString()
                    binding.idUpdate?.text = " " + list.body()?.data?.updated_at.toString()

                    binding.updateAll?.visibility = View.VISIBLE
                    binding.UpdateWorkError?.visibility = View.GONE
                } else {
                    binding.updateAll?.visibility = View.GONE
                    binding.UpdateWorkError?.visibility = View.VISIBLE
                }

            }

            mImageArrayUpdate.getReviewsSpecialist(idSpecialist)
            mImageArrayUpdate.myReviewsSpecialist.observe(viewLifecycleOwner) { user ->
                if (user.isSuccessful) {
                    binding.textReviewSpecialist.text = "(${user.body()!!.meta.total} ${resources.getText(R.string.reviews)})"
                }

            }
        } catch (e: ApiException) {
            e.printStackTrace()
            binding?.updateAll?.visibility = View.GONE
            binding?.UpdateWorkError?.visibility = View.VISIBLE
            binding?.dopTextUpdate?.visibility = View.VISIBLE
        }

    }


    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonReview.visibility = View.VISIBLE
            binding.floatingActionButtonShare.visibility = View.VISIBLE
        } else {
            binding.floatingActionButtonReview.visibility = View.INVISIBLE
            binding.floatingActionButtonShare.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonReview.startAnimation(rotateFrom)
            binding.floatingActionButtonShare.startAnimation(rotateFrom)
            binding.fabId.startAnimation(rotateOpen)
        } else {
            binding.floatingActionButtonReview.startAnimation(rotateTo)
            binding.floatingActionButtonShare.startAnimation(rotateTo)
            binding.fabId.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonReview.isClickable = true
            binding.floatingActionButtonShare.isClickable = true
        } else {
            binding.floatingActionButtonReview.isClickable = false
            binding.floatingActionButtonShare.isClickable = false
        }
    }

    private fun showSharingDialogAsKotlinWithURL(text: String, url: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "$text: $url")
        startActivity(Intent.createChooser(intent, "Share with:"))
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.CALL_PHONE)
            ) {
                uLogD("SUCCESS")
            } else {

                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {

            callPhone()
        }
    }

    private fun callPhone() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel: + ${product.promikz.AppConstants.userTelephony}"))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}