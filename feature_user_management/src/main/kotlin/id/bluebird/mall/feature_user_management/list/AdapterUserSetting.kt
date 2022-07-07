package id.bluebird.mall.feature_user_management.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.bluebird.mall.feature_user_management.R
import id.bluebird.mall.feature_user_management.databinding.ItemUserManagementBinding

class AdapterUserSetting(private val userSettingViewModel: UserManagementViewModel) :
    RecyclerView.Adapter<AdapterUserSetting.ViewHolder>() {

    private val mItems: MutableList<UserSettingCache> = ArrayList()

    class ViewHolder(
        private val viewModel: UserManagementViewModel,
        private val itemUserManagementBinding: ItemUserManagementBinding
    ) :
        RecyclerView.ViewHolder(itemUserManagementBinding.root) {
        fun setData(
            userSettingCache: UserSettingCache,
        ) {
            itemUserManagementBinding.apply {
                userCahce = userSettingCache
                userSettingVM = viewModel
            }
        }
    }

    fun addNewSetData(values: List<UserSettingCache>) {
        mItems.clear()
        mItems.addAll(values)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            userSettingViewModel,
            DataBindingUtil.inflate(
                inflater,
                R.layout.item_user_management,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val models = mItems[position]
        holder.setData(models)
    }
}