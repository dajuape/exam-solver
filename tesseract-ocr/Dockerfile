FROM debian:bullseye-slim

# Install Tesseract OCR in spanish/english
RUN apt-get update && \
    apt-get install -y tesseract-ocr \
                       tesseract-ocr-eng \
                       tesseract-ocr-spa && \
    apt-get clean


CMD ["tesseract", "--version"]
