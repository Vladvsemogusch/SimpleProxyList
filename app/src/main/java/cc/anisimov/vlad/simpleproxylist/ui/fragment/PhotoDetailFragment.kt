package cc.anisimov.vlad.simpleproxylist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import cc.anisimov.vlad.simpleproxylist.R
import cc.anisimov.vlad.simpleproxylist.ui.common.BaseFragment
import coil.load
import kotlinx.android.synthetic.main.fragment_photo_details.*

class PhotoDetailFragment : BaseFragment() {
    private val args: PhotoDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPhoto.load(args.url)
        tvTitle.text = args.title
    }
}