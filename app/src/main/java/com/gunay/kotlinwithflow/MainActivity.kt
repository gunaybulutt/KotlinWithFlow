package com.gunay.kotlinwithflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gunay.kotlinwithflow.ui.theme.KotlinWithFlowTheme
import com.gunay.kotlinwithflow.viewmodel.MyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinWithFlowTheme {

                val viewModel : MyViewModel by viewModels()

                //FirstScreen(viewModel = viewModel)
                SecondScreen(viewModel = viewModel)

            }
        }
    }
}

@Composable
fun FirstScreen(viewModel: MyViewModel){
    //değişiklik olduğunda recompositiona tabi tutar -> collectAsState(initial = 10) -- Observer gibi düşünülebilir
    //değeri alır ve state'ye çevirir compose için geliştirilmiştir
    val counter = viewModel.countDownTimerFlow.collectAsState(initial = 10)
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = counter.value.toString(),
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center))
        }
    }
    // bu kod ne yapıyor ? :  val counter = viewModel.countDownTimerFlow.collectAsState(initial = 10)
    /*
     * Bu kod parçası, bir MyViewModel instance'ı oluşturarak ve bu viewModel'ın içindeki countDownTimerFlow adlı bir Flow'u izleyerek bir state değeri oluşturuyor.
     * collectAsState(initial = 10) fonksiyonu, Flow'dan gelen değeri bir state'e dönüştürür ve bu değeri state olarak tutar. Eğer Flow herhangi bir değer yayınlarsa, budeğer state'e yansıtılır
     * ve UI otomatik olarak güncellenir.initial = 10 parametresi, Flow henüz bir değer yayınlamadığında kullanılacak başlangıç değerini belirtir. Bu durumda, counter isimli state başlangıçta
     * 10 değerine sahip olacaktır.
     */
}

@Composable
fun SecondScreen(viewModel: MyViewModel){

    //Observer ın jetpackComposet'de kullanımı gibi bişi
    val liveDataValue = viewModel.liveData.observeAsState()
    val stateFlowValue = viewModel.stateFlow.collectAsState()
    val sharedFlowValue = viewModel.sharedFlow.collectAsState(initial = "")

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
           Column(modifier = Modifier.align(Alignment.Center)){

               Text(text = liveDataValue.value ?: "")
               Button(onClick = {
                   viewModel.changeLiveDataValue()
               }) {
                   Text(text = "LiveData Button")
               }

               Spacer(modifier = Modifier.padding(10.dp))

               Text(text = stateFlowValue.value)
               Button(onClick = {
                   viewModel.changeStateFlowValue()
               }) {
                   Text(text = "StateFlow Button")
               }

               Spacer(modifier = Modifier.padding(10.dp))

               Text(text = sharedFlowValue.value)
               Button(onClick = {
                   viewModel.changeSharedFlowValue()
               }) {
                   Text(text = "SharedFlow Button")
               }
           }

        }
    }

}


