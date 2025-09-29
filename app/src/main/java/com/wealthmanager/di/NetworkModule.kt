package com.wealthmanager.di

import android.content.Context
import com.wealthmanager.data.api.FinnhubApi
import com.wealthmanager.data.api.TwseApi
import com.wealthmanager.data.api.ExchangeRateApi
import com.wealthmanager.data.service.TwseDataParser
import com.wealthmanager.data.service.ApiRetryManager
import com.wealthmanager.data.service.ApiStatusManager
import com.wealthmanager.data.service.MarketDataServiceFixedCorrected
import com.wealthmanager.data.service.MarketDataService
import com.wealthmanager.data.service.ApiErrorHandler
import com.wealthmanager.data.service.CacheManager
import com.wealthmanager.data.service.DataValidator
import com.wealthmanager.data.service.RequestDeduplicationManager
import com.wealthmanager.data.service.SmartCacheStrategy
import com.wealthmanager.data.service.PerformanceMonitor120Hz
import com.wealthmanager.data.service.ApiProviderService
import com.wealthmanager.data.repository.AssetRepository
import com.wealthmanager.utils.NumberFormatter
import com.wealthmanager.data.service.TwseCacheManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import com.google.gson.GsonBuilder

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"
    private const val TWSE_BASE_URL = "https://openapi.twse.com.tw/"
    private const val EXCHANGE_RATE_BASE_URL = "https://v6.exchangerate-api.com/"
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @Named("TWSE")
    fun provideTwseRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Use lenient JSON parsing to handle TWSE API responses
        val gson = GsonBuilder()
            .setLenient()
            .create()
        
        return Retrofit.Builder()
            .baseUrl(TWSE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    @Provides
    @Singleton
    @Named("Finnhub")
    fun provideFinnhubRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FINNHUB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideTwseApi(@Named("TWSE") twseRetrofit: Retrofit): TwseApi {
        return twseRetrofit.create(TwseApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideFinnhubApi(@Named("Finnhub") finnhubRetrofit: Retrofit): FinnhubApi {
        return finnhubRetrofit.create(FinnhubApi::class.java)
    }
    
    @Provides
    @Singleton
    @Named("ExchangeRate")
    fun provideExchangeRateRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(EXCHANGE_RATE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideExchangeRateApi(@Named("ExchangeRate") exchangeRateRetrofit: Retrofit): ExchangeRateApi {
        return exchangeRateRetrofit.create(ExchangeRateApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideTwseDataParser(debugLogManager: com.wealthmanager.debug.DebugLogManager): TwseDataParser {
        return TwseDataParser(debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideApiErrorHandler(debugLogManager: com.wealthmanager.debug.DebugLogManager): ApiErrorHandler {
        return ApiErrorHandler(debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideSmartCacheStrategy(debugLogManager: com.wealthmanager.debug.DebugLogManager): SmartCacheStrategy {
        return SmartCacheStrategy(debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideRequestDeduplicationManager(debugLogManager: com.wealthmanager.debug.DebugLogManager): RequestDeduplicationManager {
        return RequestDeduplicationManager(debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideNumberFormatter(): NumberFormatter {
        return NumberFormatter()
    }
    
    @Provides
    @Singleton
    fun providePerformanceMonitor120Hz(debugLogManager: com.wealthmanager.debug.DebugLogManager): PerformanceMonitor120Hz {
        return PerformanceMonitor120Hz(debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideCacheManager(
        assetRepository: AssetRepository,
        debugLogManager: com.wealthmanager.debug.DebugLogManager,
        smartCacheStrategy: SmartCacheStrategy
    ): CacheManager {
        return CacheManager(assetRepository, debugLogManager, smartCacheStrategy)
    }
    
    @Provides
    @Singleton
    fun provideDataValidator(debugLogManager: com.wealthmanager.debug.DebugLogManager): DataValidator {
        return DataValidator(debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideApiRetryManager(
        debugLogManager: com.wealthmanager.debug.DebugLogManager,
        apiErrorHandler: ApiErrorHandler
    ): ApiRetryManager {
        return ApiRetryManager(debugLogManager, apiErrorHandler)
    }
    
    @Provides
    @Singleton
    fun provideApiStatusManager(debugLogManager: com.wealthmanager.debug.DebugLogManager): ApiStatusManager {
        return ApiStatusManager(debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideMarketDataServiceFixedCorrected(
        apiProviderService: ApiProviderService,
        assetRepository: AssetRepository,
        debugLogManager: com.wealthmanager.debug.DebugLogManager
    ): MarketDataServiceFixedCorrected {
        return MarketDataServiceFixedCorrected(apiProviderService, assetRepository, debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideTwseCacheManager(
        debugLogManager: com.wealthmanager.debug.DebugLogManager
    ): TwseCacheManager {
        return TwseCacheManager(debugLogManager)
    }

    @Provides
    @Singleton
    fun provideApiProviderService(
        finnhubApi: FinnhubApi,
        twseApi: TwseApi,
        exchangeRateApi: ExchangeRateApi,
        twseDataParser: TwseDataParser,
        twseCacheManager: TwseCacheManager,
        debugLogManager: com.wealthmanager.debug.DebugLogManager
    ): ApiProviderService {
        return ApiProviderService(finnhubApi, twseApi, exchangeRateApi, twseDataParser, twseCacheManager, debugLogManager)
    }
    
    @Provides
    @Singleton
    fun provideMarketDataService(
        apiProviderService: ApiProviderService,
        assetRepository: AssetRepository,
        debugLogManager: com.wealthmanager.debug.DebugLogManager,
        cacheManager: CacheManager,
        apiErrorHandler: ApiErrorHandler,
        dataValidator: DataValidator,
        requestDeduplicationManager: RequestDeduplicationManager,
        apiRetryManager: ApiRetryManager,
        numberFormatter: NumberFormatter
    ): MarketDataService {
        return MarketDataService(
            apiProviderService,
            assetRepository,
            debugLogManager,
            cacheManager,
            apiErrorHandler,
            dataValidator,
            requestDeduplicationManager,
            apiRetryManager,
            numberFormatter
        )
    }
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}