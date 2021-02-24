package cc.anisimov.vlad.simpleproxylist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.anisimov.vlad.simpleproxylist.R
import cc.anisimov.vlad.simpleproxylist.domain.model.ProxyInfoUI
import cc.anisimov.vlad.simpleproxylist.domain.viewmodel.AlbumListViewModel
import cc.anisimov.vlad.simpleproxylist.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.fragment_proxy_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.toolbar.*


@AndroidEntryPoint
class ProxyListFragment : BaseFragment() {
    private val viewModel: AlbumListViewModel by viewModels()
    private lateinit var listAdapter: FlexibleAdapter<ProxyItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_proxy_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(toolbar, getString(R.string.proxy_list_title), false)
        setupSpinner()
        setupList()
        setupLoading()
        setupErrorHandling()
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.locales_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var firstTime = true
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                //  Remove trigger on data set
                if (firstTime){
                    firstTime = false
                    return
                }
                viewModel.onRegionSelected(spinner.selectedItem.toString().toLowerCase())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
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
                loadingOverlay.visibility = VISIBLE
            } else {
                loadingOverlay.visibility = GONE
            }
        }
    }

    private fun setupList() {
        rv.layoutManager = LinearLayoutManager(requireContext())
        listAdapter = FlexibleAdapter<ProxyItem>(ArrayList())
        rv.adapter = listAdapter
        viewModel.oProxyList.observe(viewLifecycleOwner) { proxyList ->
            if (proxyList == null || proxyList.isEmpty()) {
                return@observe
            }
            listAdapter.clear()
            val adapterItems = proxyList.map { ProxyItem(it) }
            listAdapter.addItems(0, adapterItems)
        }
        listAdapter.addListener(
            FlexibleAdapter.OnItemClickListener { view: View, position: Int ->
                //  Possible transition lags innate to jetpack navigation
                val item = listAdapter.getItem(position)!!
                true
            })
    }

    class ProxyItem(private val proxyInfo: ProxyInfoUI) :
        AbstractFlexibleItem<ProxyItem.ProxyViewHolder>() {

        override fun equals(other: Any?): Boolean {
            if (other is ProxyItem) {
                return this.proxyInfo.id == other.proxyInfo.id
            }
            return false
        }

        override fun getLayoutRes(): Int {
            return R.layout.item_proxy
        }

        override fun createViewHolder(
            view: View,
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>
        ): ProxyViewHolder {
            return ProxyViewHolder(view, adapter)
        }

        override fun bindViewHolder(
            adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
            holder: ProxyViewHolder,
            position: Int,
            payloads: MutableList<Any>?
        ) {
            holder.tvRegion.text = proxyInfo.region
            holder.tvId.text = proxyInfo.id.toString()
        }

        override fun hashCode(): Int {
            return proxyInfo.id.hashCode()
        }

        class ProxyViewHolder(view: View, adapter: FlexibleAdapter<*>) :
            FlexibleViewHolder(view, adapter) {
            val tvRegion: TextView = view.findViewById(R.id.tvRegion)
            val tvId: TextView = view.findViewById(R.id.tvId)
        }
    }
}