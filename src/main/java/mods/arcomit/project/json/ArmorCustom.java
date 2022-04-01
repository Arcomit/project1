package mods.arcomit.project.json;

import java.util.List;

/**
 * @Author Arcomit
 * @Update 2022/04/1-Arcomit
 * 用于添加需要自定义模型贴图的盔甲
 */
public class ArmorCustom {
    private List<String> itemName;
    private String modelResource;
    private String textureResource;
    private String modelSlimResource;

    public List<String> getItemName(){
        return itemName;
    }

    public void setItemName(List<String> itemName){
        this.itemName = itemName;
    }

    public String getModelResource(){
        return modelResource;
    }

    public void setModelResource(String modelResource){
        this.modelResource = modelResource;
    }

    public String getModelSlimResource(){
        return modelSlimResource;
    }

    public void setModelSlimResource(String modelSlimResource){
        this.modelSlimResource = modelSlimResource;
    }

    public String getTextureResource(){
        return textureResource;
    }

    public void setTextureResource(String textureResource){
        this.textureResource = textureResource;
    }
}
