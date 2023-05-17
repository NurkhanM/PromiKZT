package product.promikz.screens.sravnit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import product.promikz.*
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.AppConstants.compareAll
import product.promikz.AppConstants.getCategoryCompareID
import product.promikz.MyUtils.uToast
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.search.products.SSortActivity
import product.promikz.databinding.FragmentCampareBinding
import product.promikz.inteface.IClickListnearCompare


class CompareFragment : Fragment() {

    private var _binding: FragmentCampareBinding? = null
    private val binding get() = _binding!!

    private lateinit var mCompare: HomeViewModel
    lateinit var adapter: CompareAdapter


    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        mCompare = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentCampareBinding.inflate(inflater, container, false)
        val view = binding

        if (compareAll.isNotEmpty()) {
            ref()
        }


        view.bbtnAddCompare.setOnClickListener {
            val intent = Intent(requireActivity(), SSortActivity::class.java)
            startActivity(intent)
            (activity as AppCompatActivity).overridePendingTransition(
                R.anim.zoom_enter,
                R.anim.zoom_exit
            )
        }



        view.clearAll.setOnClickListener {
            compareAll.clear()
            isStateChecked()
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.cleared),
                Toast.LENGTH_SHORT
            ).show()
            getCategoryCompareID = -2
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

                val dataArray = list.body()?.data ?: emptyList() // Ваш массив объектов типа Data
                val sortedDataArray = dataArray.sortedWith(ProductComparator()) // Отсортированный массив
                adapter = CompareAdapter(object : IClickListnearCompare {
                    override fun clickListener(id: Int, name: String, productId: Int) {
                        uToast(requireContext(), name)
                        compareAll = removeItem(compareAll, productId)
                        isStateChecked()
                        adapter.deleteMyEducations(id)
                    }
                }, requireContext())
                binding.recyclerView.adapter = adapter
                adapter.setList(sortedDataArray)


                val info = ArrayList<String>()
                for (i in 0 until list.body()?.data?.size!!) {
                    for (field in list.body()?.data!![i].fields) {
                        val fieldInfo = "${field.name}: ${field.valueUser}"
                        info.add(fieldInfo)
                    }
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.server_error_500),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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