package com.gunay.kotlinwithflow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {

    //flow'lar otomatik olarak asenkron çalışır(coroutine içinde)
    //flow içine yazdığın şeyler sanki coroutines içinde gibi çalışır mesela buradaki delay gibi
    val countDownTimerFlow = flow<Int> {
        val countDownFrom = 10
        var counter = countDownFrom
        emit(countDownFrom)

        while (counter > 0){
            delay(1000)
            counter--
            //yayınlayıcı eğer yayınlama olmassa flowlar üzerinde işlem yapamaz.
            //emit(counter) kullanılarak counter değeri Flow nesnesine yayınlanır.
            //aynı flow içerisinde birden fazla emit() işlemi yapılabilir.
            emit(counter)
        }
        /*
        Eğer emit ile bir değer yayınlanmazsa, Flow nesnesi boş kalır ve collect işlemi sırasında değer alınamaz. Yani, countDownTimerFlow
        * kullanılarak bu Flow'u tüketen bir başka parça (collect işlemi gibi) varsa, bu parça herhangi bir değer alamaz ve geri sayım işlemi gerçekleşmez.

        "Yayınlamak" terimi, Flow içindeki değerlerin gönderilmesini ifade eder. emit fonksiyonu, Flow içindeki bir değeri göndermek için kullanılır.
        * Örneğin, emit(counter) ifadesi, her döngüdeki counter değerini Flow nesnesine gönderir. Bu sayede, bu Flow'u kullanan diğer kod blokları veya
        * parçalar, yeni değerleri alabilir ve işleyebilir.
        */
    }

    /*
    init bloğu, bir sınıfın oluşturulması sırasında çalıştırılan bir bloktur. Yani, bir sınıfın
    örneği (instance) oluşturulduğunda init bloğu otomatik olarak çalışır. Bu blok genellikle sınıfın
    başlatılması için gerekli olan işlemleri içerir.

    Örneğin, bir sınıfın başlangıç durumunu ayarlamak, bazı başlangıç değerlerini atamak veya bazı başlangıç
    işlemlerini gerçekleştirmek için init bloğu kullanılabilir. init bloğu, sınıfın diğer üyelerine erişebilir ve
    sınıfın başlatılması sırasında yürütülmesi gereken karmaşık mantıkları barındırabilir.
    */
    init {
        collectInViewModel()
    }

    //viewModel içerisinden collect etme
    //state olarak almadan kullanım için bu şekilde kullanılabilir
    private fun collectInViewModel(){
        viewModelScope.launch {
            //collect coroutine içerisinden çalışan bişi o nedenle viewModel içerisinden çağrılır
            //kısaca asenkron olarak oto ayarlanmıyor o nedenle kendin ayarlıcaksın
            //collect demeden önce filtreleme,mapping vb bazı işlemler kullanılabiliyoruz
            //bu özelliği ile flow yapıları liveData'dan daha avantajlı hale geliyor
            countDownTimerFlow
                .filter {
                    it %3 == 0
                }
                .map {
                    it + it
                }
                .collect(){
                println("counter is : ${it}")
            }

            //başka bir alternatif kullanım ama dikkat et bu .onEach gibi sonuna coroutine eklenmiyor
            //o nedenle coroutine içinde yazılmalı
            //bu örnekte alması gereken değeri alabilmesi için 2 sn beklemek zorunda olduğu için
            // son değere kadar hiçbişeyi alamaz cünkü countDownTimerFlow floww'u her 1 sn de bir
            //emit yapıyor. eğer delay olan satırı silseydik ve 2sn değilde 1sn yapaydık her değeri  print ile alabilirdik
            //fakat değerleri alsak bile bütün işlem bittikten sonra aldığı değerleri verecek.
            //delay olmassa direk verir ama
            countDownTimerFlow.collectLatest {
                delay(1000)
                println("counter is: ${it}")
            }

        }

        //.collect yerinde alternatif kullanım .onEach
        countDownTimerFlow.onEach {
            println(it)
        }.launchIn(viewModelScope)

    }


    //LiveData karsilastirmasi
    private val _liveData = MutableLiveData<String>("KotlinLiveData")
    val liveData : LiveData<String> = _liveData

    //MutableLiveData'larda ilk değeri vermek zorunda değilsin ama flowlarda zorundasın
    //stateflow flow'un daha objeleştirilmiş daha conseptleştirilmiş liveDataya benzetilmiş hali nednilebilir. d
    private val _stateFlow = MutableStateFlow("KotlinStateFlow")
    val stateFlow = _stateFlow.asStateFlow()

    //bu flow türünde değer vermek zorunda değilsin hatta değer verirsen hata verir
    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()


    fun changeLiveDataValue(){
        _liveData.value = "LiveData"
    }

    fun changeStateFlowValue(){
        _stateFlow.value = "StateFlow"
    }

    fun changeSharedFlowValue(){
        viewModelScope.launch {
            _sharedFlow.emit("SharedFlow")
        }
    }

}






















