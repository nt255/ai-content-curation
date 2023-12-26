# ai-content-curation

Automated AI Content Curation Pipeline

# Setup

1. Install Eclipse (or IntelliJ)
2. Run the following. `open pom.xml > Run > Run As > Maven Build > put "package" into goals and Run`. You'll see the jar for the dependency under Maven Dependencies in the project structure. Do this each time a new dependency is added to pom.xml.
3. If indexes are out of sync (names are not found when it's clearly there), `Project > Clean`. `mvn clean` should also work.
4. Start up comfy server.
5. Run `LocalApplicationTests` to make sure everything is working. Add the following VM arguments to the run configuration:
	* --add-exports=org.junit.platform.commons/org.junit.platform.commons.util=ALL-UNNAMED
	* --add-exports=org.junit.platform.commons/org.junit.platform.commons.logging=ALL-UNNAMED
6. Run `LocalApplication`. Server and Processor can be run separately. No VM arguments needed.

# General Conventions

- Use `final` whenever possible.
- Restrict visibility as much as possible.
- Use Lombok and Guice injections.
- Prefix abstract class names with "Base".

# Eclipse Common Mistakes

- "The declared package does not match the expected package." highlighting in red.
  - close and reopen the window

# Setting up ComfyUI to Work with Project

Last updated 12/8/23

1. Install [ComfyUI](https://github.com/comfyanonymous/ComfyUI). Follow the instructions on that page, and run ComfyUI_windows_portable/run_nvidia.gpu.bat. When its finished loading, it'll start a server on port 127:0.0.1:8188, so open that on any browser. Play around with the default workflow it loads.
2. Install [ComfyUI Manager](https://github.com/ltdrdata/ComfyUI-Manager). Open ComfyUI again, and ensure that on the sticky menu, you have access to "Manager".
3. Download the following models and put them in ComfyUI_windows_portable/ComfyUI/models/checkpoints:

   [Consistent Factor](https://civitai.com/models/9114/consistent-factor-euclid)

   and loras go in ComfyUI_windows_portable/ComfyUI/models/loras:

   [ReaLora](https://civitai.com/models/137258/realorarealistic-skin-texture)

   [Add Sharpness](https://civitai.com/models/69267?modelVersionId=76092)

   [Instant Photo X3](https://civitai.com/models/52652?modelVersionId=102533)

   and VAEs go in ComfyUI_windows_portable/ComfyUI/models/vae

   [vae-ft-mse-840000-ema-pruned](https://huggingface.co/stabilityai/sd-vae-ft-mse-original/blob/main/vae-ft-mse-840000-ema-pruned.safetensors)

4. Start ComfyUI again, and click on the gear on the top-right corner of the sticky menu. Enable Dev Mode Options. You might have to restart.
5. Drag any of the JSON workflows currently in src/processors/clients/ into the ComfyUI GUI. It should open the workflow. You'll get a big error about missing nodes. Close the menu, open Manager again, and click "install missing nodes". That should install everything, you'll probably have to restart, and then dragging the workflow back into the GUI should present no errors.
6. Play around with the text and settings a bit. In the KSampler (Advanced) node, generally results are better with higher steps, and a higher cfg tells the model to listen to your prompts more closely. But don't put them too high, or it looks bad.
7. Run the Java applcation from LocalApplication, and verify that the process runs and generates an image. It should be saved in /comfyclient_output/, either in the root directory of the drive you saved ComfyUI_windows_portable or at the same level.
