package com.uncreated.vktestapp.ui.photos;

import com.uncreated.vktestapp.model.vk.VkImage;
import com.uncreated.vktestapp.mvp.ViewBase;

import java.util.List;

public interface PhotoView extends ViewBase {

    void updateImages(List<VkImage> images);

    void updateFailed();
}
