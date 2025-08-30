package com.codecomet.linkedInProject.postService.service;

import com.codecomet.linkedInProject.postService.auth.AuthContextHolder;
import com.codecomet.linkedInProject.postService.client.ConnectionServiceClient;
import com.codecomet.linkedInProject.postService.client.UploaderServiceClient;
import com.codecomet.linkedInProject.postService.dto.Person;
import com.codecomet.linkedInProject.postService.dto.PostCreateRequestDto;
import com.codecomet.linkedInProject.postService.dto.PostDto;
import com.codecomet.linkedInProject.postService.entity.Post;
import com.codecomet.linkedInProject.postService.event.PostCreated;
import com.codecomet.linkedInProject.postService.exception.ResourceNotFoundException;
import com.codecomet.linkedInProject.postService.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionServiceClient connectionServiceClient;
    private final KafkaTemplate<Long, PostCreated> postCreatedKafkaTemplate;
    private final UploaderServiceClient uploaderServiceClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, MultipartFile file) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("Creating post for user with id: "+userId);

        ResponseEntity<String> imageUrl = uploaderServiceClient.uploadFile(file);

        Post post =modelMapper.map(postCreateRequestDto,Post.class);
        post.setUserId(userId);
        post.setImageUrl(imageUrl.getBody());
        Post savedPost = postRepository.save(post);

        //getting all the first degree connection of the user
        List<Person> firstDegreeConnectionList = connectionServiceClient.getFirstDegreeConnections(userId);


        for(Person p: firstDegreeConnectionList){  //sending notification to each connection
            PostCreated postCreated = PostCreated.builder()
                    .postId(post.getId())
                    .content(post.getContent())
                    .ownerUserId(userId)
                    .userId(p.getUserId())
                    .build();
            postCreatedKafkaTemplate.send("post_created_topic",postCreated);

        }

        return modelMapper.map(savedPost,PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        log.info("Getting post with ID: "+postId);
        Long userid = AuthContextHolder.getCurrentUserId();

        List<Person> personList = connectionServiceClient.getFirstDegreeConnections(userid);


        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found with id: "+ postId));
        return modelMapper.map(post,PostDto.class);
    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.info("Getting all the posts for user id: "+ userId);
        List<Post> posts = postRepository.findByUserId(userId);

        return posts.stream()
                .map((element) -> modelMapper.map(element,PostDto.class))
                .collect(Collectors.toList());
    }
}
