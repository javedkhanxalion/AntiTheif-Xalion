import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antitheftalarm.dont.touch.phone.finder.R
import com.example.antitheifproject.ads_manager.AdsManager
import com.example.antitheifproject.ads_manager.interfaces.NativeListener
import com.antitheftalarm.dont.touch.phone.finder.databinding.AdsItemBinding
import com.antitheftalarm.dont.touch.phone.finder.databinding.MenuItemGridLayoutBinding
import com.example.antitheifproject.model.MainMenuModel
import com.example.antitheifproject.utilities.clickWithThrottle
import com.example.antitheifproject.utilities.id_native_main_menu_screen
import com.example.antitheifproject.utilities.loadImage
import com.example.antitheifproject.utilities.val_ad_native_main_menu_screen
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class MainMenuGridAdapter(
    private val context: Activity,
    private val menuItems: List<MainMenuModel>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onClick: ((MainMenuModel, Int) -> Unit)? = null
    val VIEW_TYPE_ONE = 1
    val VIEW_TYPE_ADS = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_ONE -> {
                val binding = MenuItemGridLayoutBinding.inflate(layoutInflater, parent, false)
                MenuItemOneViewHolder(binding)
            }

            VIEW_TYPE_ADS -> {
                val binding = AdsItemBinding.inflate(layoutInflater, parent, false)
                MenuItemTwoViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (holder) {
            is MenuItemOneViewHolder -> {
                holder.bind(menuItems[position], onClick, context)
            }

            is MenuItemTwoViewHolder -> {
                holder.bind()
            }
        }
    }


    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 3) VIEW_TYPE_ADS else VIEW_TYPE_ONE
    }

    // First ViewHolder for the first layout
    inner class MenuItemOneViewHolder(private val binding: MenuItemGridLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            menuItem: MainMenuModel,
            onClick: ((MainMenuModel, Int) -> Unit)?,
            context: Context,
        ) {
            binding.topImage.loadImage(context, menuItem.icon)
            if (menuItem.iconActive)
                binding.imageActive.loadImage(context, R.drawable.active_icon)
            else
                binding.imageActive.loadImage(context, R.drawable.un_active_icon)
            binding.titleText.text = menuItem.textTitle
            binding.topView.clickWithThrottle {
                onClick?.invoke(menuItem, adapterPosition)
            }
        }
    }

    inner class MenuItemTwoViewHolder(private val binding: AdsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(){
            val adsManager = AdsManager.appAdsInit(activity = context)
            adsManager.nativeAds().loadNativeAd(
                context,
                val_ad_native_main_menu_screen,
                id_native_main_menu_screen,
                object : NativeListener {
                    override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                            val adView = context.layoutInflater.inflate(
                                R.layout.ad_unified_media_high,
                                null
                            ) as NativeAdView
                        adsManager.nativeAds().nativeViewMedia(currentNativeAd ?: return, adView)
                        binding.nativeExitAd.removeAllViews()
                        binding.nativeExitAd.addView(adView)
                        binding.adView.visibility=View.GONE
                        super.nativeAdLoaded(currentNativeAd)
                    }

                    override fun nativeAdFailed(loadAdError: LoadAdError) {
                        binding.adView.visibility=View.GONE
                        binding.nativeExitAd.visibility=View.GONE
                        super.nativeAdFailed(loadAdError)
                    }

                    override fun nativeAdValidate(string: String) {
                        binding.adView.visibility=View.GONE
                        binding.nativeExitAd.visibility=View.GONE
                        super.nativeAdValidate(string)
                    }
                })
        }
    }


}
