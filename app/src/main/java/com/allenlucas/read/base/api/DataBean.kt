package com.allenlucas.read.base.api

data class DataBean(val frontmatter: FrontMatterBean? = FrontMatterBean())

data class FrontMatterBean(
    val title: String? = "",
    val banner: BannerBean? = BannerBean(),
    val date: String? = "",
    val path: String? = "",
    val categories: List<String>? = listOf(),
    val tags: List<String>? = listOf(),
    val language: String? = "",
    val draft: String? = ""
)

data class BannerBean(val childImageSharp: ChildImageBean? = ChildImageBean())

data class ChildImageBean(val fixed: ImageBean? = ImageBean())

data class ImageBean(val src: String? = "")