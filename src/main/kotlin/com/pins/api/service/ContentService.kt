package com.pins.api.service

import com.pins.api.entities.content.Post
import com.pins.api.entities.content.PostSentiment
import com.pins.api.exceptions.FileNotFound
import com.pins.api.exceptions.PostNotFound
import com.pins.api.repository.MediaRepository
import com.pins.api.repository.PostRepository
import com.pins.api.request_response.content.PostRequest
import com.pins.api.request_response.content.SentimentRequest
import com.pins.api.utils.getAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ContentService {

    @Autowired lateinit var postRepository: PostRepository
    @Autowired lateinit var mediaRepository : MediaRepository

    fun savePost( request : PostRequest ):Post{

        val media = request.media.map { mediaRequest ->

            val mediaRef = mediaRepository.findById(mediaRequest.mediaId)
            if (!mediaRef.isPresent) throw FileNotFound()
            val media_ = mediaRef.get()
            val mediaContent = media_.tag ?: ArrayList()
            mediaContent.add(mediaRequest.content)
            media_.tag = mediaContent
            media_
        }

        val content = request.contents

        val locations = request.post_locations

        val account = getAccount()

        val comment = request.commentOf?.let { postRepository.findById(it) }
        if (comment?.isPresent == false) throw PostNotFound()

        var postToSave = Post(
            commentOf = comment?.get(),
            locations = locations,
            content = content,
            media = media,
            account = account
        )
        return postRepository.save(postToSave)
    }

    fun saveSentiment(request : SentimentRequest):Post{
        val account = getAccount()
        val postRef = postRepository.findById(request.postId)
        if (!postRef.isPresent) throw PostNotFound()
        val sentiment = PostSentiment(sentiment = request.sentiment, account = account)
        val post = postRef.get()
        post.sentiment.add(sentiment)
        return postRepository.save(post)
    }

}