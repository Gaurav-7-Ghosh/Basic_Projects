import os
from tensorflow.keras.models import load_model
from PIL import Image
import numpy as np

# Path to your saved model
MODEL_PATH = "resnet_model.keras"

# Folder containing input images
INPUT_FOLDER = "test_images"

# Load the trained model
model = load_model(MODEL_PATH)


def preprocess_image(image_path):
    image = Image.open(image_path).convert("RGB")      # Grayscale
    image = image.resize((150, 150))                 # Resize
    image_array = np.array(image) / 255.0            # Normalize
    image_array = image_array.reshape(1, 150, 150, 3) # Add batch + channel
    return image_array

def main():
    # Load model
    model = load_model(MODEL_PATH)

    # Process each image in folder
    for filename in os.listdir(INPUT_FOLDER):
        if filename.lower().endswith((".png", ".jpg", ".jpeg")):
            img_path = os.path.join(INPUT_FOLDER, filename)
            input_tensor = preprocess_image(img_path)
            prediction = model.predict(input_tensor)
            predicted_class = int(np.argmax(prediction))
            confidence = float(np.max(prediction))
            print(f"{filename}: Predicted Class = {predicted_class} (Confidence: {confidence:.2f})")

if __name__ == "__main__":
    main()