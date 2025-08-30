package com.codecomet.linkedInProject.postService.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLiked {
    private Long postId;
    private Long ownerUserId;
    private Long likedByUserId;
}
