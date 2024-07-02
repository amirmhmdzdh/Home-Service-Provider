package ir.homeservice.finalprojectsecondphase.mapper;

import ir.homeservice.finalprojectsecondphase.dto.request.CommentRequest;
import ir.homeservice.finalprojectsecondphase.dto.response.CommentResponse;
import ir.homeservice.finalprojectsecondphase.model.comment.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    Comment registerCommentToModel(CommentRequest request);

    CommentResponse modelToRegister(Comment comment);


}
