package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.model.comment.Comment;
import ir.homeservice.finalprojectsecondphase.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

}
