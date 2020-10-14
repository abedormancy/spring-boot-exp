package ga.vabe.other;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OtherPkgClass {

    static final List<Post> condition = Arrays.asList(Post.P1, Post.P2, Post.P3, Post.P4);

    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            User u1 = User.generate("u1", Post.P1, Post.P3);
            User u2 = User.generate("u2", Post.P2, Post.P3);
            User u3 = User.generate("u3", Post.P1);
            User u4 = User.generate("u4", Post.P2, Post.P3);
            User u5 = User.generate("u5", Post.P1);
            User u6 = User.generate("u6", Post.P1, Post.P2);
            User u7 = User.generate("u7", Post.P2, Post.P3);
            User u8 = User.generate("u8", Post.P3, Post.P3);
            User u9 = User.generate("u9", Post.P1, Post.P2);
            User u10 = User.generate("u10", Post.P2);
            User u11 = User.generate("u11", Post.P2, Post.P3);
            User u12 = User.generate("u12", Post.P1, Post.P3);
            List<User> users = Arrays.asList(u1, u2, u3, u4, u5, u6, u7, u8, u9, u10, u11, u12);
            Collections.shuffle(users);
            // List<User> users = Arrays.asList(u6, u3, u7, u2, u10, u1, u9, u11, u8, u4, u5, u12);
            int max = maxProjectDepartment(users);
            System.out.println("max: " + max);
            if (max < 3) {
                System.out.println(users);
            }
        }

    }

    /**
     * 人员岗位排序
     */
    private static void reSort(Map<Post, List<User>> mapper, Map<Post, Integer> postMap, Post currentPost) {
        // 对各岗位人员按岗位进行排序
        mapper.forEach((key, value) -> {
            // 人员的岗位排序
            value.forEach(p -> {
                p.getPostList().sort(Comparator.comparingInt(postMap::get));
            });

            // value.removeIf(User::isUsed);

            // 人员排序 （岗位权重越大排越前面）
            value.sort((u1, u2) -> {
                List<Post> u1Post = u1.getPostList().stream().filter(p -> p != key).collect(Collectors.toList());
                List<Post> u2Post = u2.getPostList().stream().filter(p -> p != key).collect(Collectors.toList());
                int size = u1Post.size() > u2Post.size() ? u2Post.size() : u1Post.size();
                int index = 0;
                Post p1 = null;
                for (int i = 0; i < size && index == 0; i++) {
                    p1 = u1Post.get(i);
                    index = postMap.get(u2Post.get(i)) - postMap.get(p1);
                }

                index = index == 0 ? (u1Post.size() - u2Post.size()) : index;
                if (index == 0 && p1 != null) {
                    // 权重一致，根据当前选择的岗位顺序确定权重
                    if (condition.indexOf(p1) - condition.indexOf(currentPost) > 0) {
                        return 1;
                    }
                    // 随机返回正负值的话可以达到人员随机分配项目部的效果
                    return 0;
                }
                return index;

            });
        });
    }


    /**
     * 检查岗位顺序是否改变
     */
    private static boolean verifyOrder(Map<Post, Integer> postMap, List<Post> postOrder, Post current) {
        boolean flag = true;
        for (int i = 0; i < postOrder.size() - 1 && flag; i++) {
            int index1 = postMap.get(postOrder.get(i));
            int index2 = postMap.get(postOrder.get(i + 1));
            // 岗位数量相同时，通过当前选取的岗位再进行权重比对，保证最后选取时不出错
            flag = index1 < index2 || (index1 == index2 && postOrder.indexOf(current) > i);
        }
        return flag;
    }

    /**
     * 获取最大能组建项目部的数量 <br>
     * 条件：
     * 1. 1个项目部最少需要4个不同岗位
     * 2. 1个人最多有2个不同岗位
     * 3. 1个人只能在1个项目部担任1个岗位
     */
    public static int maxProjectDepartment(List<User> userList) {
        // 分组
        Map<Post, List<User>> mapper = new HashMap<>();
        userList.stream().filter(u -> u.getPostList() != null && u.getPostList().size() > 0).forEach(u -> {
            for (Post post : u.getPostList()) {
                List<User> pl = mapper.computeIfAbsent(post, k -> new ArrayList<>());
                pl.add(u);
            }
        });
        Map<Post, Integer> postMap = new HashMap<>();
        // 岗位顺序, 通过将 postMap
        List<Post> postOrder = new ArrayList<>(4);
        mapper.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().size())).forEach(e -> {
            postOrder.add(e.getKey());
            postMap.put(e.getKey(), e.getValue().size());
        });

        reSort(mapper, postMap, Post.P1);

        boolean flag = true;
        int max = 0;
        while (flag) {
            StringBuilder sb = new StringBuilder(512);
            for (int i = 0; flag && i < condition.size(); i++) {
                List<User> users = mapper.get(condition.get(i));
                if (users == null) {
                    flag = false;
                    break;
                }
                Iterator<User> iterator = users.iterator();
                boolean matched = false;
                while (!matched && iterator.hasNext()) {
                    User next = iterator.next();
                    iterator.remove();
                    matched = !next.isUsed();
                    next.setUsed(true);
                    if (matched) {
                        for (Post post : next.getPostList()) {
                            postMap.put(post, postMap.get(post) - 1);
                        }
                        // 检查顺序是否改变，如果顺序改变需要触发重排
                        if (!verifyOrder(postMap, postOrder, condition.get(i))) {
                            reSort(mapper, postMap, condition.get(i));
                            postOrder.clear();
                            postMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).forEach(e -> {
                                postOrder.add(e.getKey());
                            });
                        }

                        sb.append(next.getName()).append(" ");
                    }
                }
                flag = matched;
            }
            if (flag) {
                max++;
                sb.insert(0, max + ". ");
                System.out.println(sb);
            }
        }
        return max;
    }

}


class User {

    private String name;

    /**
     * 岗位
     */
    private List<Post> postList;

    /**
     * 人员是否占用
     */
    private boolean used = false;

    public static User generate(String name, Post... post) {
        User person = new User();
        person.setName(name);
        person.setPostList(new ArrayList<>(Arrays.asList(post)));
        return person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return name + ": " + postList;
    }

}

/**
 * 岗位
 */
enum Post {
    P1, P2, P3, P4;
}