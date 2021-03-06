package ganymedes01.woodstuff.client;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.woodstuff.blocks.BlockWoodChest;
import ganymedes01.woodstuff.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEntityWoodChestRenderer extends TileEntitySpecialRenderer {

	private final ModelChest model_normal = new ModelChest();
	private final ModelChest model_large = new ModelLargeChest();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
		TileEntityChest chest = (TileEntityChest) tile;
		Block b = chest.getBlockType();
		if (!(b instanceof BlockWoodChest))
			return;

		BlockWoodChest block = (BlockWoodChest) b;
		int meta = chest.getBlockMetadata();

		chest.checkForAdjacentChests();

		if (chest.adjacentChestZNeg == null && chest.adjacentChestXNeg == null) {
			ModelChest model;

			boolean isNormal = chest.adjacentChestXPos == null && chest.adjacentChestZPos == null;
			model = isNormal ? model_normal : model_large;
			bindTexture(getChestTexture(block, isNormal));

			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);

			short rotation = 0;
			if (meta == 2)
				rotation = 180;
			if (meta == 3)
				rotation = 0;
			if (meta == 4)
				rotation = 90;
			if (meta == 5)
				rotation = -90;
			if (meta == 2 && chest.adjacentChestXPos != null)
				GL11.glTranslatef(1.0F, 0.0F, 0.0F);
			if (meta == 5 && chest.adjacentChestZPos != null)
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);

			GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

			float angle = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;
			float angle2;
			if (chest.adjacentChestZNeg != null) {
				angle2 = chest.adjacentChestZNeg.prevLidAngle + (chest.adjacentChestZNeg.lidAngle - chest.adjacentChestZNeg.prevLidAngle) * partialTicks;
				if (angle2 > angle)
					angle = angle2;
			}
			if (chest.adjacentChestXNeg != null) {
				angle2 = chest.adjacentChestXNeg.prevLidAngle + (chest.adjacentChestXNeg.lidAngle - chest.adjacentChestXNeg.prevLidAngle) * partialTicks;
				if (angle2 > angle)
					angle = angle2;
			}

			angle = 1.0F - angle;
			angle = 1.0F - angle * angle * angle;
			model.chestLid.rotateAngleX = -(angle * (float) Math.PI / 2.0F);
			model.renderAll();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	public static ResourceLocation getChestTexture(BlockWoodChest chest, boolean normal) {
		if (chest.isTextureGenerated(!normal))
			return chest.getTexture(!normal);

		String size = normal ? "normal" : "large";
		ResourceLocation res = getIconResource(chest.getIcon(0, 0));
		ResourceLocation out = Utils.getResource(Utils.getEntityTexture("chest_" + size + "_outline"));
		try {
			BufferedImage image = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream());
			BufferedImage outline = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(out).getInputStream());
			BufferedImage result = new BufferedImage(image.getWidth() * (normal ? 4 : 8), image.getHeight() * 4, BufferedImage.TYPE_INT_ARGB);

			if (normal) {
				copyImageOverArea(result, image, 43, 30, 12, 12);
				copyImageOverArea(result, image, 43 - 14, 30, 12, 12);
				copyImageOverArea(result, image, 43 - 28, 30, 12, 12);
				copyImageOverArea(result, image, 1, 30, 12, 12);

				copyImageOverArea(result, image, 43, 15, 12, 12);
				copyImageOverArea(result, image, 43 - 14, 15, 12, 12);
				copyImageOverArea(result, image, 43 - 28, 15, 12, 12);
				copyImageOverArea(result, image, 1, 15, 12, 12);

				copyImageOverArea(result, image, 15, 1, 12, 12);
				copyImageOverArea(result, image, 29, 1, 12, 12);
				copyImageOverArea(result, image, 15, 20, 12, 12);
				copyImageOverArea(result, image, 29, 20, 12, 12);
			} else {
				copyImageOverArea(result, image, 13, 30, 16, 12);
				copyImageOverArea(result, image, 29, 30, 16, 12);
				copyImageOverArea(result, image, 13 + 44, 30, 16, 12);
				copyImageOverArea(result, image, 29 + 44, 30, 16, 12);
				copyImageOverArea(result, image, 1, 30, 12, 12);
				copyImageOverArea(result, image, 45, 30, 12, 12);

				copyImageOverArea(result, image, 13, 15, 16, 12);
				copyImageOverArea(result, image, 29, 15, 16, 12);
				copyImageOverArea(result, image, 13 + 44, 15, 16, 12);
				copyImageOverArea(result, image, 29 + 44, 15, 16, 12);
				copyImageOverArea(result, image, 1, 15, 12, 12);
				copyImageOverArea(result, image, 45, 15, 12, 12);

				copyImageOverArea(result, image, 13, 1, 16, 12);
				copyImageOverArea(result, image, 29, 1, 16, 12);
				copyImageOverArea(result, image, 13 + 30, 1, 16, 12);
				copyImageOverArea(result, image, 29 + 30, 1, 16, 12);

				copyImageOverArea(result, image, 13, 20, 16, 12);
				copyImageOverArea(result, image, 29, 20, 16, 12);
				copyImageOverArea(result, image, 13 + 30, 20, 16, 12);
				copyImageOverArea(result, image, 29 + 30, 20, 16, 12);
			}

			result = merge(result, outline);

			ResourceLocation loc = chest.getTexture(!normal);
			Minecraft.getMinecraft().getTextureManager().loadTexture(loc, new DynamicTexture(result));
			chest.setTextureGenerated(!normal);
			return loc;
		} catch (IOException e) {
			e.printStackTrace();
			return res;
		}
	}

	private static ResourceLocation getIconResource(IIcon icon) {
		if (icon == null)
			return null;
		String iconName = icon.getIconName();
		if (iconName == null)
			return null;

		String string = "minecraft";

		int colonIndex = iconName.indexOf(58);
		if (colonIndex >= 0) {
			if (colonIndex > 1)
				string = iconName.substring(0, colonIndex);

			iconName = iconName.substring(colonIndex + 1, iconName.length());
		}

		string = string.toLowerCase();
		iconName = "textures/blocks/" + iconName + ".png";
		return new ResourceLocation(string, iconName);
	}

	private static void copyImageOverArea(BufferedImage img1, BufferedImage img2, int x, int y, int width, int height) {
		x = (x * img2.getWidth()) / 16;
		y = (y * img2.getHeight()) / 16;
		width = (width * img2.getWidth()) / 16;
		height = (height * img2.getHeight()) / 16;
		int h = (img2.getHeight() - height) / 2;
		int w = (img2.getWidth() - width) / 2;

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				img1.setRGB(i + x, j + y, img2.getRGB(w + i, h + j));
	}

	private static BufferedImage merge(BufferedImage img1, BufferedImage... imgs) {
		BufferedImage merged = copy(img1);

		for (BufferedImage img2 : imgs) {
			if (merged.getWidth() != img2.getWidth() || merged.getHeight() != img2.getHeight()) {
				float wScale = (float) merged.getWidth() / (float) img2.getWidth();
				float hScale = (float) merged.getHeight() / (float) img2.getHeight();

				img2 = scale(img2, wScale, hScale);
			}

			merged = blend(merged, img2);
		}

		return merged;
	}

	private static BufferedImage copy(BufferedImage src) {
		BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		Object srcbuf, dstbuf;
		int length;

		if (src.getRaster().getDataBuffer().getDataType() == DataBuffer.TYPE_BYTE) {
			srcbuf = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
			dstbuf = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
			length = ((byte[]) dstbuf).length;
		} else {
			srcbuf = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
			dstbuf = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();
			length = ((int[]) dstbuf).length;
		}

		System.arraycopy(srcbuf, 0, dstbuf, 0, length);

		return dst;
	}

	private static BufferedImage blend(BufferedImage image, BufferedImage overlay) {
		int w = Math.max(image.getWidth(), overlay.getWidth());
		int h = Math.max(image.getHeight(), overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics g = combined.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(overlay, 0, 0, null);

		return combined;
	}

	private static BufferedImage scale(BufferedImage image, float widthScale, float heightScale) {
		BufferedImage img = new BufferedImage((int) (image.getWidth() * widthScale), (int) (image.getHeight() * heightScale), image.getType());

		Graphics2D grph = (Graphics2D) img.getGraphics();
		grph.scale(widthScale, heightScale);
		grph.drawImage(image, 0, 0, null);
		grph.dispose();

		return img;
	}
}