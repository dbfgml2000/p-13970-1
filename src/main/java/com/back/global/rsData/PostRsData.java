package com.back.global.rsData;

import com.back.domain.post.post.dto.PostDto;

public record PostRsData(String resultCode, String msg, PostDto data) {
}
