package product.promikz.screens.favorite

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import product.promikz.AppConstants.TOKEN_USER
import product.promikz.R
import product.promikz.viewModels.HomeViewModel
import product.promikz.screens.update.UpdateActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import product.promikz.MyUtils
import product.promikz.databinding.FragmentFavoriteBinding
import product.promikz.databinding.ItemMakeupModelsBinding
import product.promikz.inteface.IClickListnearHomeFavorite

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: FavoriteAdapter
    private lateinit var mFavoriteVM: HomeViewModel
    private lateinit var vieww: View


    @Suppress("DEPRECATION")
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mFavoriteVM = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val view = binding


        adapter = FavoriteAdapter(object : IClickListnearHomeFavorite {
            override fun clickListener(baseID: Int, viewAdapter: ItemMakeupModelsBinding, isLike: Boolean, position: Int) {
                if (!isLike) {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite2)
                } else {
                    viewAdapter.imgFavorite.setImageResource(R.drawable.ic_favorite)
                }

                mFavoriteVM.postLike("Bearer $TOKEN_USER", baseID)

                adapter.deleteMyEducations(position)
            }

            override fun clickListener2(baseID: Int, adver: Int, favorite: String) {
                if (adver != 3) {
                    val intent = Intent(requireActivity(), UpdateActivity::class.java)
                    intent.putExtra("hello", baseID)
                    startActivity(intent)
                    (activity as AppCompatActivity).overridePendingTransition(
                        R.anim.zoom_enter,
                        R.anim.zoom_exit
                    )

                } else {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(favorite))
                        startActivity(browserIntent)
                    }catch (e: ActivityNotFoundException){
                        MyUtils.uToast(requireContext(), resources.getString(R.string.error_link))
                    }
                }
            }

        })

        recyclerView = view.rvFavoritesActivity
        val space = 30
        val includeEdge = true
        recyclerView.addItemDecoration(SpaceItemDecoration(space, includeEdge))
        recyclerView.adapter = adapter

        ref()

        view.swipeRefreshLayoutFavorites.setOnRefreshListener {
            ref()
            view.swipeRefreshLayoutFavorites.isRefreshing = false
        }

        view.clickUserBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        return view.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun ref() {
        mFavoriteVM.getFavorite("Bearer $TOKEN_USER")
        mFavoriteVM.myFavoriteList.observe(viewLifecycleOwner) { list ->

            if (list.isSuccessful) {
                binding.swipeRefreshLayoutFavorites.visibility = View.GONE
                binding.FavoritesWorkError.visibility = View.VISIBLE
                list.body()?.data?.let { adapter.setData(it) }
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    binding.swipeRefreshLayoutFavorites.visibility = View.VISIBLE
                    binding.FavoritesWorkError.visibility = View.GONE
                }

            } else {
                binding.swipeRefreshLayoutFavorites.visibility = View.GONE
                binding.FavoritesWorkError.visibility = View.VISIBLE
            }
            adapter.notifyDataSetChanged()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        ref()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}