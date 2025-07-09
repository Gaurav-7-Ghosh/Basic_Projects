FROM python:3.9-slim

# Copy app source + model weights
COPY main.py /app/
COPY resnet_model.keras /app/
COPY test_images/ /app/

# Set working dir
WORKDIR /app

# Install Python deps
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt



CMD ["python", "main.py"]