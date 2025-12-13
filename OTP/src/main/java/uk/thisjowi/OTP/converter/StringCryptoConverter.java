package uk.thisjowi.OTP.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.thisjowi.OTP.util.EncryptionUtil;

@Converter
public class StringCryptoConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return EncryptionUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return EncryptionUtil.decrypt(dbData);
    }
}
