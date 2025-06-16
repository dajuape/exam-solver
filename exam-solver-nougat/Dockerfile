FROM ubuntu:22.04

# Install Python (with venv), pip, and system dependencies
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
      python3 python3-venv python3-pip git \
      tesseract-ocr libgl1 libglib2.0-0 build-essential && \
    rm -rf /var/lib/apt/lists/*

# Create and activate virtual environment
RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:${PATH}"

WORKDIR /workspace

# Upgrade pip and install CPU-only PyTorch
RUN pip install --upgrade pip && \
    pip install torch torchvision torchaudio --extra-index-url https://download.pytorch.org/whl/cpu

# Clone and install Nougat, pin compatible versions
RUN git clone https://github.com/facebookresearch/nougat.git
WORKDIR /workspace/nougat
RUN pip install nougat-ocr[api] && \
    pip install "albumentations<1.3" "transformers<4.29"

# Expose API port
EXPOSE 8503

# Start the API with Uvicorn
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8503"]

