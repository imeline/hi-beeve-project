package beeve.biz.fitness.service

import beeve.biz.fitness.dto.request.FitnessCreateRequest

interface FitnessService {

    fun createFitness(memberId: Long, request: FitnessCreateRequest)
}