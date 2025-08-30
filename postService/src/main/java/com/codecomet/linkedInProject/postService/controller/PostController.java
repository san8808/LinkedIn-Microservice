package com.codecomet.linkedInProject.postService.controller;

import com.codecomet.linkedInProject.postService.auth.AuthContextHolder;
import com.codecomet.linkedInProject.postService.dto.PostCreateRequestDto;
import com.codecomet.linkedInProject.postService.dto.PostDto;
import com.codecomet.linkedInProject.postService.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/core")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> createPost(@RequestPart("post") PostCreateRequestDto postCreateRequestDto,
                                              @RequestPart("file")MultipartFile file){
        PostDto postDto = postService.createPost(postCreateRequestDto,file);
        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPosts(@PathVariable Long postId){

        Long userId = AuthContextHolder.getCurrentUserId();

        // TODO: Remove in future
        // Call the connection service from the Post service and pass the userId inside the headers

        PostDto postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsOfUser(@PathVariable Long userId){
        List<PostDto> postDto = postService.getAllPostsOfUser(userId);
        return ResponseEntity.ok(postDto);
    }


}
