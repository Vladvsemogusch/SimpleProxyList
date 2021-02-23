package cc.anisimov.vlad.simpleproxylist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.anisimov.vlad.simpleproxylist.R
import cc.anisimov.vlad.simpleproxylist.domain.model.PhotoUI
import cc.anisimov.vlad.simpleproxylist.domain.viewmodel.PhotoListViewModel
import cc.anisimov.vlad.simpleproxylist.ui.common.BaseFragment
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.fragment_album_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.toolbar.*

@AndroidEntryPoint
class PhotoListFragment : BaseFragment() {
    private val viewModel: PhotoListViewModel by viewModels()
    private lateinit var listAdapter: FlexibleAdapter<PhotoAdapterItem>
    private val args: PhotoListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.start(args.albumId)
        setupToolbar(toolbar, getString(R.string.photos))
        setupList()
        setupLoading()
        setupErrorHandling()
    }

    private fun setupErrorHandling() {
        viewModel.oError.observe(viewLifecycleOwner) { errorText ->
            if (errorText != null) {
                showSimpleDialog(errorText)
            }
        }
    }


    private fun setupLoading() {
        viewModel.oLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                loadingOverlay.visibility = View.VISIBLE
            } else {
                loadingOverlay.visibility = View.GONE
            }
        }
    }

    private fun setupList() {
        rv.layoutManager = GridLayoutManager(requireContext(), 2)
        listAdapter = FlexibleAdapter<PhotoAdapterItem>(ArrayList())
        rv.adapter = listAdapter
        viewModel.oPhotoList.observe(viewLifecycleOwner) { photoList ->
            if (photoList == null || photoList.isEmpty()) {
                return@observe
            }
            val adapterItems = photoList.map { PhotoAdapterItem(it) }
            listAdapter.addItems(0, adapterItems)
        }
        listAdapter.addListener(
            FlexibleAdapter.OnItemClickListener { view: View, position: Int ->
                val item = listAdapter.getItem(position)!!
                val photoUrl = item.photoUrl
                val photoTitle = item.photoTitle
                val directions =
                    PhotoListFragmentDirections.actionPhotoListToDetail(photoUrl, photoTitle)
                findNavController().navigate(directions)
                true
            })
    }

    class PhotoAdapterItem(private val photo: PhotoUI) :
        AbstractFlexibleItem<PhotoAdapterItem.PhotoViewHolder>() {
        val photoUrl
            get() = photo.url
        val photoTitle
            get() = photo.title

        override fun equals(other: Any?): Boolean {
            if (other is PhotoAdapterItem) {
                return this.photo.id == other.photo.id
            }
            return false
        }

        override fun getLayoutRes(): Int {
            return R.layout.item_photo
        }

        override fun createViewHolder(
            view: View,
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
        ): PhotoViewHolder {
            return PhotoViewHolder(view, adapter)
        }

        override fun bindViewHolder(
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
            holder: PhotoViewHolder,
            position: Int,
            payloads: MutableList<Any>?
        ) {
            holder.ivThumbnail.load(photo.thumbnailUrl)
            holder.tvPhotoTitle.text = photo.title
        }

        override fun hashCode(): Int {
            return photo.id.hashCode()
        }

        class PhotoViewHolder(view: View, adapter: FlexibleAdapter<*>) :
            FlexibleViewHolder(view, adapter) {
            val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
            val tvPhotoTitle: TextView = view.findViewById(R.id.tvPhotoTitle)
        }
    }
}