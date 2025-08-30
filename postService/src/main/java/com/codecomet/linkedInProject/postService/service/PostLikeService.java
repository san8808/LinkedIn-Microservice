package com.codecomet.linkedInProject.postService.service;

import com.codecomet.linkedInProject.postService.auth.AuthContextHolder;
import com.codecomet.linkedInProject.postService.client.ConnectionServiceClient;
import com.codecomet.linkedInProject.postService.entity.Post;
import com.codecomet.linkedInProject.postService.entity.PostLike;
import com.codecomet.linkedInProject.postService.event.PostLiked;
import com.codecomet.linkedInProject.postService.exception.BadRequestException;
import com.codecomet.linkedInProject.postService.exception.ResourceNotFoundException;
import com.codecomet.linkedInProject.postService.repository.PostLikeRepository;
import com.codecomet.linkedInProject.postService.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionServiceClient connectionServiceClient;
    private final KafkaTemplate<Long, PostLiked> postLikedKafkaTemplate;

    @Transactional
    public void likePost(Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("user with id: {} liking the post with id: {}", userId,postId);

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found wiht id: " + postId));

        boolean hasAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId,postId);
        if(hasAlreadyLiked) throw new BadRequestException("You cannot like the post again");

        PostLike postLike = new PostLike();
        postLike.setPostId(post.getId());
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        //todo: send notifications to the owner of the post
        PostLiked postLiked = PostLiked.builder()
                .postId(postId)
                .likedByUserId(userId)
                .ownerUserId(post.getUserId())
                .build();

        postLikedKafkaTemplate.send("post_liked_topic",postLiked);

    }

    @Transactional
    public void unlikePost(Long postId) {
        Long userId= AuthContextHolder.getCurrentUserId();
        log.info("user with id: {} liking the post with id: {}", userId,postId);

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found wiht id: " + postId));

        boolean hasAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId,postId);
        if(!hasAlreadyLiked) throw new BadRequestException("You cannot unlike the post that you have not liked");

        postLikeRepository.deleteByUserIdAndPostId(userId,postId);



    }
}
