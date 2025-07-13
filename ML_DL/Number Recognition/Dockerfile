FROM python:3.9-slim


# Set working dir
WORKDIR /app

# Copy app source + model weights
COPY main.py /app/
COPY main.py .
COPY resnet_model.keras .
COPY test_images/ ./test_images



# Install Python deps
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt



CMD ["python", "main.py"]