package com.duop.analyzer.service;

import com.duop.analyzer.entity.Group;
import com.duop.analyzer.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public List<String> getAllUniversityFlows() {
        List<Group> allGroups = groupRepository.findAll();
        return allGroups.stream()
                .map(group -> group.getName() + "-" + group.getNumber().toString().substring(0, group.getNumber().toString().length() - 1))
                .map(flow -> flow.concat("X"))
                .toList();
    }
}
