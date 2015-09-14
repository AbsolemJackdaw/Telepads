package net.subaraki.telepads.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTelepad extends ModelBase {
    // fields
    ModelRenderer shape1;
    ModelRenderer shape2;
    ModelRenderer shape3;
    ModelRenderer shape4;
    ModelRenderer shape5;
    ModelRenderer shape6;
    ModelRenderer shape7;
    ModelRenderer shape8;
    ModelRenderer shape9;
    ModelRenderer shape10;
    
    public ModelTelepad() {
        textureWidth = 64;
        textureHeight = 32;
        
        shape1 = new ModelRenderer(this, 0, 0);
        shape1.addBox(-5F, 0F, -5F, 10, 1, 10);
        shape1.setRotationPoint(0F, 22F, 0F);
        shape1.setTextureSize(64, 32);
        shape1.mirror = true;
        setRotation(shape1, 0F, 0F, 0F);
        shape2 = new ModelRenderer(this, 0, 0);
        shape2.addBox(-3F, 0F, 0F, 3, 1, 2);
        shape2.setRotationPoint(-5F, 22F, 0F);
        shape2.setTextureSize(64, 32);
        shape2.mirror = true;
        setRotation(shape2, -0.2F, 0F, -0.4F);
        shape3 = new ModelRenderer(this, 0, 0);
        shape3.addBox(-3F, 0F, -2F, 3, 1, 2);
        shape3.setRotationPoint(-5F, 22F, 0F);
        shape3.setTextureSize(64, 32);
        shape3.mirror = true;
        setRotation(shape3, 0.2F, 0F, -0.4F);
        shape4 = new ModelRenderer(this, 0, 11);
        shape4.addBox(-5F, -0.5F, -5F, 10, 1, 10);
        shape4.setRotationPoint(0F, 23F, 0F);
        shape4.setTextureSize(64, 32);
        shape4.mirror = true;
        setRotation(shape4, 0F, 0.8F, 0F);
        shape5 = new ModelRenderer(this, 0, 0);
        shape5.addBox(0F, 0F, 0F, 3, 1, 2);
        shape5.setRotationPoint(5F, 22F, 0F);
        shape5.setTextureSize(64, 32);
        shape5.mirror = true;
        setRotation(shape5, -0.2F, 0F, 0.4F);
        shape6 = new ModelRenderer(this, 0, 0);
        shape6.addBox(0F, 0F, -2F, 3, 1, 2);
        shape6.setRotationPoint(5F, 22F, 0F);
        shape6.setTextureSize(64, 32);
        shape6.mirror = true;
        setRotation(shape6, 0.2F, 0F, 0.4F);
        shape7 = new ModelRenderer(this, 0, 0);
        shape7.addBox(-2F, 0F, -3F, 2, 1, 3);
        shape7.setRotationPoint(0F, 22F, -5F);
        shape7.setTextureSize(64, 32);
        shape7.mirror = true;
        setRotation(shape7, 0.4F, 0.1F, -0.2F);
        shape8 = new ModelRenderer(this, 0, 0);
        shape8.addBox(0F, 0F, -3F, 2, 1, 3);
        shape8.setRotationPoint(0F, 22F, -5F);
        shape8.setTextureSize(64, 32);
        shape8.mirror = true;
        setRotation(shape8, 0.4F, -0.05F, 0.2F);
        shape9 = new ModelRenderer(this, 0, 0);
        shape9.addBox(0F, 0F, 0F, 2, 1, 3);
        shape9.setRotationPoint(0F, 22F, 5F);
        shape9.setTextureSize(64, 32);
        shape9.mirror = true;
        setRotation(shape9, -0.4F, 0.1F, 0.2F);
        shape10 = new ModelRenderer(this, 0, 0);
        shape10.addBox(-2F, 0F, 0F, 2, 1, 3);
        shape10.setRotationPoint(0F, 22F, 5F);
        shape10.setTextureSize(64, 32);
        shape10.mirror = true;
        setRotation(shape10, -0.4F, -0.05F, -0.2F);
    }
    
    @Override
    public void render (Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        render(f5);
    }
    
    public void render (float f5) {
        
        shape1.render(f5);
        shape2.render(f5);
        shape3.render(f5);
        shape4.render(f5);
        shape5.render(f5);
        shape6.render(f5);
        shape7.render(f5);
        shape8.render(f5);
        shape9.render(f5);
        shape10.render(f5);
        
    }
    
    private void setRotation (ModelRenderer model, float x, float y, float z) {
        
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
