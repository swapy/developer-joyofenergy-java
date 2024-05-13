package uk.tw.energy.infrastructure.error;

// NOTE future enhancements to add more fine grained details
public record ErrorMessage(String errorCode, String message) {}
