package product.promikz.screens.sravnit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import product.promikz.*
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.compareAll
import product.promikz.AppConstants.getCategoryCompareID
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.search.SSortActivity
import product.promikz.MyUtils.uLogD
import product.promikz.databinding.FragmentCampareBinding


class CompareFragment : Fragment() {

    private var _binding: FragmentCampareBinding? = null
    private val binding get() = _binding!!


    private val ed: ArrayList<ArrayList<ArrayList<ArrayList<String>>>> = ArrayList()

    private lateinit var mCompare: HomeViewModel
    private lateinit var vieww: View

    private var idCategory: Int = -1
    private var idCategory2: Int = -1
    private var idCategory3: Int = -1
    private var idCategory4: Int = -1


    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        mCompare = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCampareBinding.inflate(inflater, container, false)
        val view = binding


        if (compareAll.isNotEmpty()) {
            ref()
        }

        isStateChecked()



        view.bbtnAddCompare.setOnClickListener {
            val intent = Intent(requireActivity(), SSortActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
        }




        view.ivImageDelete.setOnClickListener {
            view.comapareConL1.visibility = View.GONE
            compareAll = removeItem(compareAll, idCategory)
            isStateChecked()
            Toast.makeText(requireContext(),
                resources.getText(R.string.deleted_youre_products),
                Toast.LENGTH_SHORT).show()
        }
        view.ivImageDelete2.setOnClickListener {
            view.comapareConL2.visibility = View.GONE
            compareAll = removeItem(compareAll, idCategory2)
            isStateChecked()
            Toast.makeText(requireContext(),
                resources.getText(R.string.deleted_youre_products),
                Toast.LENGTH_SHORT).show()
        }
        view.ivImageDelete3.setOnClickListener {
            view.comapareConL3.visibility = View.GONE
            compareAll = removeItem(compareAll, idCategory3)
            isStateChecked()
            Toast.makeText(requireContext(),
                resources.getText(R.string.deleted_youre_products),
                Toast.LENGTH_SHORT).show()
        }
        view.ivImageDelete4.setOnClickListener {
            view.comapareConL4.visibility = View.GONE
            compareAll = removeItem(compareAll, idCategory4)
            isStateChecked()
            Toast.makeText(requireContext(),
                resources.getText(R.string.deleted_youre_products),
                Toast.LENGTH_SHORT).show()
        }




        view.clearAll.setOnClickListener {
            compareAll.clear()
            isStateChecked()
            Toast.makeText(requireContext(),
                resources.getText(R.string.cleared),
                Toast.LENGTH_SHORT).show()
            getCategoryCompareID = -2
            view.comapareConL1.visibility = View.GONE
            view.comapareConL2.visibility = View.GONE
            view.comapareConL3.visibility = View.GONE
            view.comapareConL4.visibility = View.GONE
        }


        view.clickCompareBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        return view.root
    }


    private fun ref() {
        mCompare.getCompareID("Bearer $TOKEN_USER", getCategoryCompareID, compareAll.joinToString())
        mCompare.myGetCompare.observe(viewLifecycleOwner) { list ->


            if (list.isSuccessful) {

                for (c in 0 until list.body()?.data?.size!!) {

                    val all = ArrayList<ArrayList<ArrayList<String>>>()
                    val info2 = ArrayList<ArrayList<String>>()
                    val info = ArrayList<String>()
                    info.add(list.body()?.data!![c].img)
                    info.add(list.body()?.data!![c].name)
                    info.add(list.body()?.data!![c].price.toString())
                    info.add(list.body()?.data!![c].state.toString())
                    info.add(list.body()?.data!![c].city.name)
                    info.add(list.body()?.data!![c].brand.name)
                    info.add(list.body()?.data!![c].id.toString())
                    info2.add(info)
                    all.add(info2)
                    val allParams = ArrayList<ArrayList<String>>()
                    for (i in 0 until list.body()?.data!![c].fields.size) {
                        val params = ArrayList<String>()
                        params.add(list.body()?.data!![c].fields[i].name)
                        params.add(list.body()?.data!![c].fields[i].valueUser)
                        allParams.add(params)
                    }
                    all.add(allParams)
                    ed.add(all)

                }

                stateFieldsSize(ed)
            } else{
                Toast.makeText(requireContext(), resources.getText(R.string.server_error_500), Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun stateFieldsSize(ar: ArrayList<ArrayList<ArrayList<ArrayList<String>>>>) {

        if (ar.size == 1) {
            fieldsUpload(ar[0])
        }

        if (ar.size == 2) {
            fieldsUpload(ar[0])
            fieldsUpload2(ar[1])
        }
        if (ar.size == 3) {
            fieldsUpload(ar[0])
            fieldsUpload2(ar[1])
            fieldsUpload3(ar[2])
        }
        if (ar.size == 4) {
            fieldsUpload(ar[0])
            fieldsUpload2(ar[1])
            fieldsUpload4(ar[3])
            fieldsUpload3(ar[2])
        }

    }


    @SuppressLint("SetTextI18n")
    private fun fieldsUpload(ar: ArrayList<ArrayList<ArrayList<String>>>) {
        binding.comapareConL1.visibility = View.VISIBLE

        Glide.with(requireContext()).load(ar[0][0][0])
            .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
            .into(binding.ivMedia)
        binding.fieldsName.text = ar[0][0][1]
        if (ar[0][0][2] != "0"){
            binding.fieldsPrice.text = ar[0][0][2]
        }else{
            binding.fieldsPrice.text = "Договорная"
        }
        if (ar[0][0][3] == "1"){
            binding.fieldsState.text = "Новая"
        }else{
            binding.fieldsState.text = "Б/у"
        }
        binding.fieldsCity.text = ar[0][0][4]
        binding.fieldsBrand.text = ar[0][0][5]
        idCategory = ar[0][0][6].toInt()

        val layoutAll = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        val layoutTextTile = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutTextTile.marginStart = 20
        layoutTextTile.marginEnd = 20


        val layoutText = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val layoutView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 10
        )

        binding.fieldsLiner.removeAllViews()

        for (i in 0 until ar[1].size) {


            val linearLayout = LinearLayout(binding.fieldsLiner.context)
            linearLayout.removeAllViews()
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = layoutAll


            val vView = View(linearLayout.context)
            vView.setBackgroundColor(getColorFromAttr(com.prongbang.dialog.R.attr.colorOnPrimary))
            vView.layoutParams = layoutView


            val textView = TextView(linearLayout.context)
            textView.layoutParams = layoutTextTile
            textView.textSize = 12f
            textView.text = ar[1][i][0]
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))


            val textView2 = TextView(linearLayout.context)
            textView2.layoutParams = layoutText
            textView2.textSize = 16f
            textView2.gravity = Gravity.CENTER
            textView2.text = ar[1][i][1]



            linearLayout.addView(vView)
            linearLayout.addView(textView)
            linearLayout.addView(textView2)
            binding.fieldsLiner.addView(linearLayout)

        }
    }


    @SuppressLint("SetTextI18n")
    private fun fieldsUpload2(ar: ArrayList<ArrayList<ArrayList<String>>>) {
        binding.comapareConL2.visibility = View.VISIBLE

        Glide.with(requireContext()).load(ar[0][0][0])
            .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
            .into(binding.ivMedia2)
        if (ar[0][0][2] != "0"){
            binding.fieldsPrice2.text = ar[0][0][2]
        }else{
            binding.fieldsPrice2.text = "Договорная"
        }
        if (ar[0][0][3] == "1"){
            binding.fieldsState2.text = "Новая"
        }else{
            binding.fieldsState2.text = "Б/у"
        }
        binding.fieldsCity2.text = ar[0][0][4]
        binding.fieldsBrand2.text = ar[0][0][5]
        idCategory2 = ar[0][0][6].toInt()

        val layoutAll = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        val layoutTextTile = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutTextTile.marginStart = 20
        layoutTextTile.marginEnd = 20


        val layoutText = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val layoutView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 10
        )

        binding.fieldsLiner2.removeAllViews()

        for (i in 0 until ar[1].size) {


            val linearLayout = LinearLayout(binding.fieldsLiner2.context)
            linearLayout.removeAllViews()
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = layoutAll


            val vView = View(linearLayout.context)
            vView.setBackgroundColor(getColorFromAttr(com.prongbang.dialog.R.attr.colorOnPrimary))
            vView.layoutParams = layoutView


            val textView = TextView(linearLayout.context)
            textView.layoutParams = layoutTextTile
            textView.textSize = 12f
            textView.text = ar[1][i][0]
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))


            val textView2 = TextView(linearLayout.context)
            textView2.layoutParams = layoutText
            textView2.textSize = 16f
            textView2.gravity = Gravity.CENTER
            textView2.text = ar[1][i][1]



            linearLayout.addView(vView)
            linearLayout.addView(textView)
            linearLayout.addView(textView2)
            binding.fieldsLiner2.addView(linearLayout)

        }
    }


    @SuppressLint("SetTextI18n")
    private fun fieldsUpload3(ar: ArrayList<ArrayList<ArrayList<String>>>) {
        binding.comapareConL3.visibility = View.VISIBLE
        Glide.with(requireContext()).load(ar[0][0][0])
            .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
            .into(binding.ivMedia3)
        binding.fieldsName3.text = ar[0][0][1]

        if (ar[0][0][2].toInt() != 0){
            binding.fieldsPrice3.text = ar[0][0][2]
        }else{
            binding.fieldsPrice3.text = "Договорная"
        }

        if (ar[0][0][3] == "1"){
            binding.fieldsState3.text = "Новая"
        }else{
            binding.fieldsState3.text = "Б/у"
        }
        binding.fieldsCity3.text = ar[0][0][4]
        binding.fieldsBrand3.text = ar[0][0][5]
        idCategory3 = ar[0][0][6].toInt()

        val layoutAll = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        val layoutTextTile = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutTextTile.marginStart = 20
        layoutTextTile.marginEnd = 20


        val layoutText = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val layoutView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 10
        )

        binding.fieldsLiner3.removeAllViews()

        for (i in 0 until ar[1].size) {


            val linearLayout = LinearLayout(binding.fieldsLiner3.context)
            linearLayout.removeAllViews()
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = layoutAll


            val vView = View(linearLayout.context)
            vView.setBackgroundColor(getColorFromAttr(com.prongbang.dialog.R.attr.colorOnPrimary))
            vView.layoutParams = layoutView


            val textView = TextView(linearLayout.context)
            textView.layoutParams = layoutTextTile
            textView.textSize = 12f
            textView.text = ar[1][i][0]
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))


            val textView2 = TextView(linearLayout.context)
            textView2.layoutParams = layoutText
            textView2.textSize = 16f
            textView2.gravity = Gravity.CENTER
            textView2.text = ar[1][i][1]



            linearLayout.addView(vView)
            linearLayout.addView(textView)
            linearLayout.addView(textView2)
            binding.fieldsLiner3.addView(linearLayout)

        }
    }


    @SuppressLint("SetTextI18n")
    private fun fieldsUpload4(ar: ArrayList<ArrayList<ArrayList<String>>>) {
        binding.comapareConL4.visibility = View.VISIBLE

        Glide.with(requireContext()).load(ar[0][0][0])
            .thumbnail(Glide.with(requireContext()).load(R.drawable.loader2))
            .into(binding.ivMedia4)
        binding.fieldsName4.text = ar[0][0][1]

        if (ar[0][0][2] != "0"){
            binding.fieldsPrice4.text = ar[0][0][2]
        }else{
            binding.fieldsPrice4.text = "Договорная"
        }

        if (ar[0][0][3] == "1"){
            binding.fieldsState4.text = "Новая"
        }else{
            binding.fieldsState4.text = "Б/у"
        }
        binding.fieldsCity4.text = ar[0][0][4]
        binding.fieldsBrand4.text = ar[0][0][5]
        idCategory4 = ar[0][0][6].toInt()

        val layoutAll = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        val layoutTextTile = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutTextTile.marginStart = 20
        layoutTextTile.marginEnd = 20


        val layoutText = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val layoutView = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 10
        )

        binding.fieldsLiner4.removeAllViews()

        for (i in 0 until ar[1].size) {


            val linearLayout = LinearLayout(binding.fieldsLiner4.context)
            linearLayout.removeAllViews()
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = layoutAll


            val vView = View(linearLayout.context)
            vView.setBackgroundColor(getColorFromAttr(com.prongbang.dialog.R.attr.colorOnPrimary))
            vView.layoutParams = layoutView


            val textView = TextView(linearLayout.context)
            textView.layoutParams = layoutTextTile
            textView.textSize = 12f
            textView.text = ar[1][i][0]
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))


            val textView2 = TextView(linearLayout.context)
            textView2.layoutParams = layoutText
            textView2.textSize = 16f
            textView2.gravity = Gravity.CENTER
            textView2.text = ar[1][i][1]



            linearLayout.addView(vView)
            linearLayout.addView(textView)
            linearLayout.addView(textView2)
            binding.fieldsLiner4.addView(linearLayout)

        }
    }

    @ColorInt
    private fun getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true,
    ): Int {
        context?.theme?.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }


    private fun removeItem(array: ArrayList<Int>, value: Int): ArrayList<Int> {
        val arr = array.filter { it != value }.toIntArray()
        return arr.toCollection(ArrayList())
    }

    private fun isStateChecked() {

        if (compareAll.isEmpty()) {
            binding.comapareConLNull.visibility = View.VISIBLE
        } else {
            binding.comapareConLNull.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}