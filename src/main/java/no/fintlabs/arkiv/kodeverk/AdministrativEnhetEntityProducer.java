package no.fintlabs.arkiv.kodeverk;

//@Component
//public class AdministrativEnhetEntityProducer extends FintKafkaEntityProducer {
//
//    public AdministrativEnhetEntityProducer(KafkaTemplate<String, Object> kafkaTemplate, FintClient fintClient) {
//        super(kafkaTemplate, fintClient);
//    }
//
//    @Override
//    protected String getEndpointUrl() {
//        return "/arkiv/noark/administrativenhet";
//    }
//
//    @Scheduled(fixedDelay = 30000L, initialDelay = 10000L)
//    public void pollingSchedule() {
//        this.pollResources();
//    }
//}
