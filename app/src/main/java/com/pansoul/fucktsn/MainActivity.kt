package com.pansoul.fucktsn

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化视图
        initViews()
    }
    
    private fun initViews() {
        // 添加顶部图标动画
        val iconView = findViewById<ImageView>(R.id.app_icon)
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeIn.duration = 1500
        iconView.startAnimation(fadeIn)
        
        // 添加卡片动画效果
        val cardFeatures = findViewById<CardView>(R.id.card_features)
        val cardInstructions = findViewById<CardView>(R.id.card_instructions)
        val cardAbout = findViewById<CardView>(R.id.card_about)
        
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slideUp.duration = 800
        
        cardFeatures.startAnimation(slideUp)
        
        slideUp.startOffset = 200
        cardInstructions.startAnimation(slideUp)
        
        slideUp.startOffset = 400
        cardAbout.startAnimation(slideUp)
        
        // 设置版本信息
        val versionText = findViewById<TextView>(R.id.version_text)
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionText.text = "版本: ${pInfo.versionName}"
        } catch (e: Exception) {
            versionText.text = "版本: 1.0"
        }
        
        // 设置主题颜色
        setThemeColors()
        
        // 设置GitHub按钮点击事件
        val githubButton = findViewById<Button>(R.id.github_button)
        githubButton.setOnClickListener {
            // 打开GitHub链接
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse("https://github.com/pansoul1/Fuck-TSN")
            startActivity(intent)
        }
    }
    
    private fun setThemeColors() {
        // 获取卡片
        val cardFeatures = findViewById<CardView>(R.id.card_features)
        val cardInstructions = findViewById<CardView>(R.id.card_instructions)
        val cardAbout = findViewById<CardView>(R.id.card_about)
        
        // 设置卡片不同的颜色边框
        cardFeatures.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_bg))
        cardInstructions.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_bg))
        cardAbout.setCardBackgroundColor(ContextCompat.getColor(this, R.color.card_bg))
        
        // 给标题文本设置主题颜色
        val featuresTitle = findViewById<TextView>(R.id.features_title)
        val instructionsTitle = findViewById<TextView>(R.id.instructions_title)
        val aboutTitle = findViewById<TextView>(R.id.about_title)
        
        featuresTitle.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
        instructionsTitle.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
        aboutTitle.setTextColor(ContextCompat.getColor(this, R.color.primary_color))
    }
}