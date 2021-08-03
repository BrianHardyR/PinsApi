package com.pins.api.services

import com.pins.api.entities.*
import com.pins.api.repo.*
import com.pins.api.security.AppUserDetails
import com.pins.api.utils.getLoggedInUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * This handles all content related operations
 */

@Service
class ContentService {
    /**
     * Search tags ( elastic search )
     * post content
     * upload media with captions
     */

    @Autowired
    lateinit var postsRepo: PostsRepo

    @Autowired
    lateinit var authenticationService: AuthenticationService

    @Autowired
    lateinit var tagRepo: TagRepo

    @Autowired
    lateinit var contentTagRepo: ContentTagRepo

    @Autowired
    lateinit var locationTagRepo: LocationTagRepo

    @Autowired
    lateinit var routeTagRepo: RouteTagRepo

    @Autowired
    lateinit var userMentionsTagRepo: UserMentionTagRepo

    @Autowired
    lateinit var coordinatesTagRepo: CoordinatesTagRepo

    @Autowired
    lateinit var accountsRepo: AccountsRepo

    @Autowired
    lateinit var userRepo: UserRepo

    val loggedInUser: AppUserDetails get() = getLoggedInUserDetails()

    @Transactional(rollbackFor = [Exception::class])
    fun createPost(request: PostRequest): Post {
        val post = Post(
            content = request.content,
            belongsTo = accountsRepo.findById(loggedInUser.account.ID!!).get(),
            postedBy = userRepo.findById(loggedInUser.user.ID!!).get(),
        )
        request.location?.run {
            var locationTags = map {
                Location(coords = it.coord).apply {
                    ID = it.id
                    name = it.tagname
                    type = TAG_TYPES.Location
                }
            }
            post.locations.addAll(locationTags)
        }
        request.route?.run {
            val routeTags = map {
                it.id?.run { routeTagRepo.findById(this).get()} ?: Route(from = it.from, to = it.to).apply {
                    name = it.tagname
                    type = TAG_TYPES.Route
                    println("new route $ID")
                }.let {
                    val routeRef = routeTagRepo.findByRouteKey(it.routeKey)
                    if (routeRef.isPresent) routeRef.get() else it
                }
            }
            post.routes.addAll(routeTagRepo.saveAll(routeTags))
        }
        request.userMentions?.run {
            val userMentionTags = map {
                val userMentionTag = UserMentions(userModel = userRepo.findById(it.userId).get())
                userMentionTag.name = it.tagname
                userMentionTag.type = TAG_TYPES.Mentions
                userMentionTag
            }
            post.userMentions.addAll(userMentionTags)
        }


        return postsRepo.save(post)
//        return post
    }


}

data class PostRequest(
    val content: String,
    val location: List<LocationRequest>? = null,
    val route: List<RouteRequest>? = null,
    val media: List<MediaRequest>? = null,
    val userMentions: List<UserMentionsRequest>? = null,
    val tags: List<TagRequest>? = null
)

data class LocationRequest(
    val id: Long? = null,
    val tagname: String,
    val coord: Coordinates
)

data class RouteRequest(
    val id: Long? = null,
    val tagname: String,
    val from: Coordinates,
    val to: Coordinates
)

data class MediaRequest(
    val caption: String,
    val file: String
)

data class CommentRequest(
    val postId: Long,
    val comment: PostRequest
)

data class UserMentionsRequest(
    val userId: Long,
    val tagname: String
)

data class TagRequest(
    val tagname: String
)