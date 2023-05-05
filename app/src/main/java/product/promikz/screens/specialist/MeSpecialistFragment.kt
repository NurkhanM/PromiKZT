package product.promikz.screens.specialist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.shop.SpecialistReviewBSH
import com.google.android.gms.common.api.ApiException
import product.promikz.AppConstants.MY_SPECIALIST
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.databinding.FragmentMeSpecialistBinding

class MeSpecialistFragment : Fragment() {

    private var _binding: FragmentMeSpecialistBinding? = null
    private val binding get() = _binding!!


    private lateinit var mImageArrayUpdate: HomeViewModel

    private var nameProducts: String = ""
    private val url: String = "https://promi.kz/specialist/"
    private var urlProducts: String = ""

    //    private var idSpecialist: Int = -1

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
        _binding = FragmentMeSpecialistBinding.inflate(inflater, container, false)
        val view = binding

        view.fabId.setOnClickListener {
            onAddButtonClicked()
        }


        view.floatingActionButtonReview.setOnClickListener {
            if (TOKEN_USER != "null") {
                (activity as AppCompatActivity).supportFragmentManager.beginTransaction().add(SpecialistReviewBSH(), "specialist").commit()
            } else {
                Toast.makeText(requireContext(), resources.getText(R.string.need_sign_up), Toast.LENGTH_SHORT)
                    .show()
            }

        }

        view.floatingActionButtonShare.setOnClickListener {
            showSharingDialogAsKotlinWithURL(nameProducts, urlProducts)
        }


        view.updateCallBtn.setOnClickListener {
            Navigation.findNavController(view.root)
                .navigate(R.id.action_meSpecialistFragment_to_changeSpecialistFragment)

        }

        view.clickUpdateBackCard.setOnClickListener {
            activity?.onBackPressed()
        }


        return view.root
    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.updateAll?.visibility = View.GONE
        binding.updateCallBtn?.visibility = View.GONE
        binding.dopTextUpdate?.visibility = View.VISIBLE
        binding.UpdateWorkError?.visibility = View.VISIBLE

        try {
            if (MY_SPECIALIST == -1) {
                binding.updateAll?.visibility = View.GONE
                binding.updateCallBtn?.visibility = View.GONE
                binding.UpdateWorkError?.visibility = View.VISIBLE
                binding.dopTextUpdate?.visibility = View.VISIBLE
            } else {
                mImageArrayUpdate.getSpecialistShow("Bearer $TOKEN_USER", MY_SPECIALIST)
                mImageArrayUpdate.mySpecialistShow.observe(viewLifecycleOwner) { list ->

                    if (list.isSuccessful) {

                        Glide.with(requireContext())
                            .load(list.body()?.data?.img)
                            .thumbnail(Glide.with(requireActivity()).load(R.drawable.loader2))
                            .centerCrop()
                            .into(binding.imgAddFirst)

                        binding.textNameUser.text = list.body()?.data?.user?.name
                        binding.textExperience.text = list.body()?.data?.experience.toString()
                        binding.textLocationInfo.text = list.body()?.data?.city?.name
                        binding.textNumberUser.text = list.body()?.data?.user?.phone
                        binding.textEmailUser.text = list.body()?.data?.user?.email

                        nameProducts = list.body()?.data?.user?.name.toString()
                        urlProducts = url + list.body()?.data?.id.toString()

                        if (list.body()?.data?.departure.toString() == "1") {
                            binding.textDeparture.text = resources.getText(R.string.ready_move)
                        } else {
                            binding.textDeparture.text = resources.getText(R.string.not)
                        }


                        if (list.body()?.data?.skills?.isNotEmpty() == true){
                            val array = list.body()?.data?.skills!!
                            val arrayString = ArrayList<String>()
                            array.forEach{
                                arrayString.add(it.name)
                            }
                            binding.textSkills.text = arrayString.joinToString(prefix = "", postfix = "", separator = ", ") // <1•2•3•4•5•6>

                        }else{
                            binding.textSkills.text = resources.getText(R.string.not_skills)
                        }

                        if (list.body()?.data?.categories?.isNotEmpty() == true){
                            val array = list.body()?.data?.categories!!
                            val arrayString = ArrayList<String>()
                            array.forEach{
                                arrayString.add(it.name)
                            }
                            binding.textCategory.text = arrayString.joinToString(prefix = "", postfix = "", separator = ", ") // <1•2•3•4•5•6>

                        }else{
                            binding.textCategory.text = resources.getText(R.string.not_skills)
                        }

                        if (list.body()?.data?.specializations?.isNotEmpty() == true){
                            val array = list.body()?.data?.specializations!!
                            val arrayString = ArrayList<String>()
                            array.forEach{
                                arrayString.add(it.name)
                            }
                            binding.textSpecialization.text = arrayString.joinToString(prefix = "", postfix = "", separator = ", ") // <1•2•3•4•5•6>

                        }else{
                            binding.textSpecialization.text = resources.getText(R.string.not_skills)
                        }

                        if (list.body()?.data?.ratingsAvg == null) {
                            binding.svgRating.rating = 0.0f
                            binding.numberRatingAVG.text = resources.getText(R.string.not_rating)
                        } else {
                            binding.svgRating.rating =
                                list.body()?.data?.ratingsAvg?.toString()?.toFloat()!!
                            binding.numberRatingAVG.text = binding.svgRating.rating.toString()
                        }

                        binding.idProduct?.text = list.body()?.data?.id.toString()
                        binding.idCreate?.text = list.body()?.data?.created_at.toString()
                        binding.idUpdate?.text = list.body()?.data?.updated_at.toString()
                        binding.updateAll?.visibility = View.VISIBLE
                        binding.updateCallBtn?.visibility = View.VISIBLE
                        binding.UpdateWorkError?.visibility = View.GONE
                        binding.UpdateWorkError?.visibility = View.GONE
                    } else {
                        binding.updateAll?.visibility = View.GONE
                        binding.updateCallBtn?.visibility = View.GONE
                        binding.UpdateWorkError?.visibility = View.VISIBLE
                        binding.UpdateWorkError?.visibility = View.VISIBLE
                    }

                }

                mImageArrayUpdate.getReviewsSpecialist(MY_SPECIALIST)
                mImageArrayUpdate.myReviewsSpecialist.observe(viewLifecycleOwner) { user ->
                    if (user.isSuccessful) {
                        binding.textReviewSpecialist.text = "(${user.body()!!.meta.total} ${resources.getText(R.string.reviews)})"
                    }

                }
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            binding.updateAll?.visibility = View.GONE
            binding.updateCallBtn?.visibility = View.GONE
            binding.UpdateWorkError?.visibility = View.VISIBLE
            binding.dopTextUpdate?.visibility = View.VISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}