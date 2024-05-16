package kopo.poly.dto;

import lombok.Builder;

@Builder
public record MailDTO (

        String toWho,
        String title,
        String contents

) {
}
