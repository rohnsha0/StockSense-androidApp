package com.rohnsha.stocksense

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.rohnsha.stocksense.databinding.ActivityServiceStatusBinding
import com.rohnsha.stocksense.serviceAPI.obj_service.serviceStatusAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class service_status : AppCompatActivity() {

    private lateinit var bindingService: ActivityServiceStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingService= ActivityServiceStatusBinding.inflate(layoutInflater)
        setContentView(bindingService.root)


        val dynamicUrlService= "https://wznfqzcqzrov2epwv7me4tm46a0rhvse.lambda-url.ap-south-1.on.aws/"

        lifecycleScope.launch(Dispatchers.IO){
            try {
                val serviceResponse= serviceStatusAPI.getServiceStatus(dynamicUrlService)
                withContext(Dispatchers.Main){
                    bindingService.tvStatusH2.visibility= View.VISIBLE
                    bindingService.appBarService.visibility= View.VISIBLE
                    bindingService.loadingViewService.visibility= View.GONE
                    bindingService.statusContainer.visibility= View.VISIBLE
                    bindingService.tvStatusVal1.text= serviceResponse.status
                }
                if (serviceResponse.comment1=="na"){
                    bindingService.comment1.visibility= View.GONE
                    bindingService.comment1Tv.visibility= View.GONE
                } else {
                    bindingService.comment1.text= serviceResponse.comment1.substringAfter('_')
                    bindingService.comment1Tv.text= serviceResponse.comment1.substringBefore('_')
                }
                if (serviceResponse.comment2=="na"){
                    bindingService.comment2.visibility= View.GONE
                    bindingService.comment2Tv.visibility= View.GONE
                } else {
                    bindingService.comment2.text= serviceResponse.comment2.substringAfter('_')
                    bindingService.comment2Tv.text= serviceResponse.comment2.substringBefore('_')
                }
                if (serviceResponse.comment3=="na"){
                    bindingService.comment3.visibility= View.GONE
                    bindingService.comment3Tv.visibility= View.GONE
                } else {
                    bindingService.comment3.text= serviceResponse.comment3.substringAfter('_')
                    bindingService.comment3Tv.text= serviceResponse.comment3.substringBefore('_')
                }
            } catch (e: Exception){
                Log.d("serviceStatusAPI", e.toString())
            }
        }
    }

}