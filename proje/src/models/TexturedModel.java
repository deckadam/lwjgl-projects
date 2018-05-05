package models;

import textures.ModelTexture;

public class TexturedModel {
	private RawModel rawmodel;
	private ModelTexture mdlTexture;
	public TexturedModel(RawModel model,ModelTexture texture) {
		this.rawmodel=model;
		this.mdlTexture=texture;
	}
	public RawModel getRawmodel() {
		return rawmodel;
	}
	public ModelTexture getMdlTexture() {
		return mdlTexture;
	}
	
}
