package com.uncreated.vktestapp.ui.friends;

import com.uncreated.vktestapp.model.vk.VkUser;
import com.uncreated.vktestapp.mvp.ViewBase;

import java.util.List;

public interface FriendsView extends ViewBase {

    void setFriends(List<VkUser> friends);
}
